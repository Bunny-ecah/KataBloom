package com.example.myproject.learning;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Bundled Malay word practice: 6 levels x 20 words, each tagged with a theme.
 * Syllable cues are visual scaffolds, not phoneme audio. Levels 0-2 keep their original
 * meaning (and first words) so saved results from earlier versions stay correct.
 */
public final class LessonBank {
    public static final int LEVELS = 6, MODES = 6, THEMES = 6, PER_LEVEL = 20, ROUND_SIZE = 5;
    /** Learning order shown to learners (level ids, easiest first). */
    public static final int[] ORDER = {0, 1, 3, 4, 2, 5};
    public static final int LISTEN = 0, INITIAL = 1, BUILD = 2, SPEAK = 3, PICTURE = 4, MISSING = 5;
    public static final int ANIMALS = 0, FOOD = 1, HOME = 2, BODY = 3, NATURE = 4, TRAVEL = 5;

    public static final class Word {
        public final String text, syllables, picture;
        public final int theme, level;
        Word(String text, String syllables, String picture, int theme, int level) {
            this.text = text; this.syllables = syllables; this.picture = picture; this.theme = theme; this.level = level;
        }
        public String[] parts() { return syllables.split(" "); }
    }

    private static final Word[][] WORDS = {
        { // 0 · Vowel seeds: words beginning with a vowel
            w("api","a pi","🔥",NATURE,0), w("ayam","a yam","🐔",ANIMALS,0), w("ikan","i kan","🐟",ANIMALS,0), w("ibu","i bu","👩",HOME,0), w("ular","u lar","🐍",ANIMALS,0),
            w("itik","i tik","🦆",ANIMALS,0), w("ubi","u bi","🍠",FOOD,0), w("awan","a wan","☁️",NATURE,0), w("ulat","u lat","🐛",ANIMALS,0), w("ubat","u bat","💊",HOME,0),
            w("air","a ir","💧",NATURE,0), w("anak","a nak","👧",HOME,0), w("epal","e pal","🍎",FOOD,0), w("oren","o ren","🍊",FOOD,0), w("ais","a is","🧊",FOOD,0),
            w("angin","a ngin","🌬️",NATURE,0), w("udang","u dang","🦐",ANIMALS,0), w("ombak","om bak","🌊",NATURE,0), w("anggur","ang gur","🍇",FOOD,0), w("atap","a tap","🏠",HOME,0)
        },
        { // 1 · Word garden: two open syllables
            w("buku","bu ku","📚",HOME,1), w("bola","bo la","⚽",HOME,1), w("susu","su su","🥛",FOOD,1), w("mata","ma ta","👁️",BODY,1), w("kaki","ka ki","🦶",BODY,1),
            w("roti","ro ti","🍞",FOOD,1), w("gigi","gi gi","🦷",BODY,1), w("baju","ba ju","👕",BODY,1), w("nasi","na si","🍚",FOOD,1), w("topi","to pi","🧢",BODY,1),
            w("lori","lo ri","🚚",TRAVEL,1), w("kuda","ku da","🐴",ANIMALS,1), w("rusa","ru sa","🦌",ANIMALS,1), w("madu","ma du","🍯",FOOD,1), w("batu","ba tu","🪨",NATURE,1),
            w("kopi","ko pi","☕",FOOD,1), w("labu","la bu","🎃",FOOD,1), w("kota","ko ta","🏰",TRAVEL,1), w("padi","pa di","🌾",NATURE,1), w("dadu","da du","🎲",HOME,1)
        },
        { // 2 · Syllable tree: three syllables
            w("kelapa","ke la pa","🥥",FOOD,2), w("kereta","ke re ta","🚗",TRAVEL,2), w("kepala","ke pa la","🙂",BODY,2), w("menara","me na ra","🗼",TRAVEL,2), w("pelangi","pe la ngi","🌈",NATURE,2),
            w("tomato","to ma to","🍅",FOOD,2), w("kamera","ka me ra","📷",HOME,2), w("sekolah","se ko lah","🏫",TRAVEL,2), w("basikal","ba si kal","🚲",TRAVEL,2), w("harimau","ha ri mau","🐅",ANIMALS,2),
            w("kelawar","ke la war","🦇",ANIMALS,2), w("beruang","be ru ang","🐻",ANIMALS,2), w("kerusi","ke ru si","🪑",HOME,2), w("jambatan","jam ba tan","🌉",TRAVEL,2), w("telinga","te li nga","👂",BODY,2),
            w("tembikai","tem bi kai","🍉",FOOD,2), w("seluar","se lu ar","👖",BODY,2), w("cendawan","cen da wan","🍄",NATURE,2), w("zirafah","zi ra fah","🦒",ANIMALS,2), w("selipar","se li par","🩴",BODY,2)
        },
        { // 3 · Closed syllables: two syllables ending in a consonant
            w("rumah","ru mah","🏡",HOME,3), w("gajah","ga jah","🐘",ANIMALS,3), w("katak","ka tak","🐸",ANIMALS,3), w("pisang","pi sang","🍌",FOOD,3), w("kucing","ku cing","🐈",ANIMALS,3),
            w("kasut","ka sut","👟",BODY,3), w("bulan","bu lan","🌙",NATURE,3), w("bintang","bin tang","⭐",NATURE,3), w("lampu","lam pu","💡",HOME,3), w("pokok","po kok","🌳",NATURE,3),
            w("tikus","ti kus","🐭",ANIMALS,3), w("lebah","le bah","🐝",ANIMALS,3), w("kapal","ka pal","🚢",TRAVEL,3), w("jagung","ja gung","🌽",FOOD,3), w("tangan","ta ngan","✋",BODY,3),
            w("pensel","pen sel","✏️",HOME,3), w("gunting","gun ting","✂️",HOME,3), w("lembu","lem bu","🐄",ANIMALS,3), w("hidung","hi dung","👃",BODY,3), w("pintu","pin tu","🚪",HOME,3)
        },
        { // 4 · ng & ny sounds
            w("bunga","bu nga","🌸",NATURE,4), w("nyamuk","nya muk","🦟",ANIMALS,4), w("nyanyi","nya nyi","🎤",HOME,4), w("monyet","mo nyet","🐒",ANIMALS,4), w("penyu","pe nyu","🐢",ANIMALS,4),
            w("tangga","tang ga","🪜",HOME,4), w("singa","si nga","🦁",ANIMALS,4), w("bangunan","ba ngu nan","🏢",TRAVEL,4), w("langit","la ngit","🌤️",NATURE,4), w("nyiur","nyi ur","🌴",NATURE,4),
            w("payung","pa yung","☂️",BODY,4), w("angsa","ang sa","🦢",ANIMALS,4), w("kangkung","kang kung","🥬",FOOD,4), w("bawang","ba wang","🧅",FOOD,4), w("lengan","le ngan","💪",BODY,4),
            w("kerang","ke rang","🐚",ANIMALS,4), w("minyak","mi nyak","🫒",FOOD,4), w("dinding","din ding","🧱",HOME,4), w("bangau","ba ngau","🐦",ANIMALS,4), w("pinggan","ping gan","🍽️",HOME,4)
        },
        { // 5 · Long words
            w("matahari","ma ta ha ri","☀️",NATURE,5), w("helikopter","he li kop ter","🚁",TRAVEL,5), w("kalendar","ka len dar","📅",HOME,5), w("televisyen","te le vi syen","📺",HOME,5), w("komputer","kom pu ter","💻",HOME,5),
            w("ambulans","am bu lans","🚑",TRAVEL,5), w("keretapi","ke re ta pi","🚆",TRAVEL,5), w("perpustakaan","per pus ta ka an","🏛️",TRAVEL,5), w("belalang","be la lang","🦗",ANIMALS,5), w("kanggaru","kang ga ru","🦘",ANIMALS,5),
            w("buaya","bu a ya","🐊",ANIMALS,5), w("dinosaur","di no sa ur","🦕",ANIMALS,5), w("telefon","te le fon","📱",HOME,5), w("kalkulator","kal ku la tor","🧮",HOME,5), w("hospital","hos pi tal","🏥",TRAVEL,5),
            w("halilintar","ha li lin tar","⚡",NATURE,5), w("motosikal","mo to si kal","🏍️",TRAVEL,5), w("penguin","pe ngu in","🐧",ANIMALS,5), w("brokoli","bro ko li","🥦",FOOD,5), w("gelombang","ge lom bang","🏄",NATURE,5)
        }
    };
    private static Word w(String text, String parts, String picture, int theme, int level) { return new Word(text, parts, picture, theme, level); }

    public static boolean scored(int mode) { return mode == INITIAL || mode == BUILD || mode == PICTURE || mode == MISSING; }

    /** Five words per round; four rotating sets cover all 20 words of a level. */
    public static Word word(int level, int round, int index) {
        if (level < 0 || level >= LEVELS || round < 0 || index < 0 || index >= ROUND_SIZE) throw new IllegalArgumentException("Invalid lesson position");
        return WORDS[level][((round % 4) * ROUND_SIZE + index) % PER_LEVEL];
    }
    public static List<Word> level(int level) { return Collections.unmodifiableList(Arrays.asList(WORDS[level])); }
    public static List<Word> all() {
        List<Word> result = new ArrayList<>();
        for (int id : ORDER) result.addAll(Arrays.asList(WORDS[id]));
        return result;
    }
    public static Word find(String text) { for (Word[] level : WORDS) for (Word word : level) if (word.text.equals(text)) return word; return null; }

    public static List<String> initials(Word word, int seed) {
        String answer = word.text.substring(0, 1);
        List<String> choices = new ArrayList<>(); choices.add(answer);
        for (String s : new String[]{"a","i","u","b","d","p","k","m","s","t"}) if (!s.equals(answer)) { choices.add(s); if (choices.size() == 3) break; }
        Collections.rotate(choices, Math.floorMod(seed, 3)); return choices;
    }
    public static List<String> tiles(Word word, int seed) {
        List<String> values = new ArrayList<>(Arrays.asList(word.parts()));
        Collections.rotate(values, 1 + Math.floorMod(seed, values.size()));
        if (String.join("", values).equals(word.text)) Collections.reverse(values);
        return values;
    }
    /** The answer plus two other words from the same level that start differently where possible. */
    public static List<String> pictureChoices(Word word, int seed) {
        Word[] pool = WORDS[word.level];
        List<String> choices = new ArrayList<>(); choices.add(word.text);
        int start = Math.floorMod(seed * 7 + 3, pool.length);
        for (int pass = 0; pass < 2 && choices.size() < 3; pass++)
            for (int i = 0; i < pool.length && choices.size() < 3; i++) {
                Word other = pool[(start + i) % pool.length];
                if (choices.contains(other.text)) continue;
                if (pass == 0 && other.text.charAt(0) == word.text.charAt(0)) continue;
                choices.add(other.text);
            }
        Collections.rotate(choices, Math.floorMod(seed, 3)); return choices;
    }
    /** Hidden letter position for Missing Letter: never the first letter (that is Find the First Letter). */
    public static int gap(Word word, int seed) { return 1 + Math.floorMod(seed * 3 + word.text.length(), word.text.length() - 1); }
    public static String masked(Word word, int gap) { return word.text.substring(0, gap) + "_" + word.text.substring(gap + 1); }
    public static List<String> gapChoices(Word word, int gap, int seed) {
        String answer = word.text.substring(gap, gap + 1);
        boolean vowel = "aeiou".contains(answer);
        String[] pool = vowel ? new String[]{"a","i","u","e","o"} : new String[]{"b","d","k","m","n","p","s","t","l","g","r"};
        List<String> choices = new ArrayList<>(); choices.add(answer);
        int start = Math.floorMod(seed, pool.length);
        for (int i = 0; i < pool.length && choices.size() < 3; i++) { String s = pool[(start + i) % pool.length]; if (!choices.contains(s)) choices.add(s); }
        Collections.rotate(choices, Math.floorMod(seed + 1, 3)); return choices;
    }
    public static String normalize(String value) { return value == null ? "" : value.toLowerCase(Locale.ROOT).replaceAll("[^\\p{L}\\p{N}]", ""); }
    public static boolean matches(String target, List<String> candidates) {
        if (candidates == null || normalize(target).isEmpty()) return false;
        for (String candidate : candidates) if (normalize(target).equals(normalize(candidate))) return true; return false;
    }
    private LessonBank() {}
}
