import { before, beforeEach, after, test } from 'node:test';
import { readFileSync } from 'node:fs';
import { initializeTestEnvironment, assertSucceeds, assertFails } from '@firebase/rules-unit-testing';
import { doc, collection, getDoc, getDocs, setDoc, updateDoc, deleteDoc, serverTimestamp, setLogLevel } from 'firebase/firestore';

// Refuse to run without the local emulator. Never use the app's real project ID.
if (!process.env.FIRESTORE_EMULATOR_HOST) throw new Error('Run npm run test:rules with the demo emulator.');
setLogLevel('silent'); // Expected permission denials are verified by assertions, not console noise.
let env;
const parent = (uid) => env.authenticatedContext(uid).firestore();
const backup = (db, uid = 'alice') => doc(db, `users/${uid}/dyslearn/state`);
const valid = () => ({ payload: '{"version":2,"children":[],"records":[]}', updatedAt: serverTimestamp() });

before(async () => {
  const [host, port] = process.env.FIRESTORE_EMULATOR_HOST.split(':');
  env = await initializeTestEnvironment({
    projectId: 'demo-katabloom',
    firestore: { host, port: Number(port), rules: readFileSync('firestore.rules', 'utf8') }
  });
});
beforeEach(async () => env.clearFirestore());
after(async () => { if (env) await env.cleanup(); });

test('a parent can write and read their own backup', async () => {
  const ref = backup(parent('alice'));
  await assertSucceeds(setDoc(ref, valid()));
  await assertSucceeds(getDoc(ref));
});
test('another parent cannot read or overwrite a backup', async () => {
  await assertSucceeds(setDoc(backup(parent('alice')), valid()));
  await assertFails(getDoc(backup(parent('bob'))));
  await assertFails(setDoc(backup(parent('bob')), valid()));
});
test('signed-out users cannot read or write a backup', async () => {
  const ref = backup(env.unauthenticatedContext().firestore());
  await assertFails(getDoc(ref));
  await assertFails(setDoc(ref, valid()));
});
test('backup rejects unexpected fields', async () => {
  await assertFails(setDoc(backup(parent('alice')), { ...valid(), owner: 'bob' }));
});
test('backup rejects non-string payloads', async () => {
  await assertFails(setDoc(backup(parent('alice')), { ...valid(), payload: {} }));
});
test('backup rejects an oversized payload', async () => {
  await assertFails(setDoc(backup(parent('alice')), { ...valid(), payload: 'x'.repeat(700001) }));
});
test('backup requires a server timestamp', async () => {
  await assertFails(setDoc(backup(parent('alice')), { payload: '{}' }));
  await assertFails(setDoc(backup(parent('alice')), { payload: '{}', updatedAt: 'yesterday' }));
});
test('valid updates must refresh the server timestamp', async () => {
  const ref = backup(parent('alice'));
  await assertSucceeds(setDoc(ref, valid()));
  await assertFails(updateDoc(ref, { payload: '{"changed":true}' }));
  await assertSucceeds(updateDoc(ref, { payload: '{"changed":true}', updatedAt: serverTimestamp() }));
});
test('only the owning parent can delete the backup', async () => {
  await assertSucceeds(setDoc(backup(parent('alice')), valid()));
  await assertFails(deleteDoc(backup(parent('bob'))));
  await assertSucceeds(deleteDoc(backup(parent('alice'))));
});
test('a parent can manage and list their own child profiles', async () => {
  const db = parent('alice');
  await assertSucceeds(setDoc(doc(db, 'users/alice/children/child1'), { name: 'Alya', age: '8' }));
  await assertSucceeds(getDocs(collection(db, 'users/alice/children')));
  await assertSucceeds(deleteDoc(doc(db, 'users/alice/children/child1')));
});
test('other families cannot access child profiles or their result subcollections', async () => {
  const db = parent('bob');
  await assertFails(getDocs(collection(db, 'users/alice/children')));
  await assertFails(setDoc(doc(db, 'users/alice/children/child1'), { name: 'Changed' }));
  await assertFails(getDoc(doc(db, 'users/alice/children/child1/speech_results/result1')));
});
test('lesson content is read-only for authenticated parents', async () => {
  await env.withSecurityRulesDisabled(async context => {
    await setDoc(doc(context.firestore(), 'quiz_questions/question1'), { word: 'api' });
  });
  await assertSucceeds(getDoc(doc(parent('alice'), 'quiz_questions/question1')));
  await assertFails(setDoc(doc(parent('alice'), 'quiz_questions/question1'), { word: 'changed' }));
  await assertFails(getDoc(doc(env.unauthenticatedContext().firestore(), 'quiz_questions/question1')));
});
