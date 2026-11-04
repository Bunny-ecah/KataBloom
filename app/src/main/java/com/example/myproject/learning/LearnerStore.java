package com.example.myproject.learning;

import android.content.Context;
import android.content.SharedPreferences;
import org.json.*;
import java.util.*;

/** Device-private, account-separated state. Never stores passwords or audio. */
public final class LearnerStore {
    private final SharedPreferences prefs;
    private JSONObject state;
    public boolean recovered;
    public LearnerStore(Context context,String owner){
        prefs=context.getSharedPreferences("dyslearn_v2_"+owner,Context.MODE_PRIVATE);
        String raw=prefs.getString("state","");
        try{state=raw.isEmpty()?empty():validate(new JSONObject(raw));}
        catch(JSONException|IllegalArgumentException e){prefs.edit().putString("recovery",raw).apply();state=empty();recovered=true;}
    }
    private static JSONObject empty(){JSONObject o=new JSONObject();put(o,"version",2);put(o,"children",new JSONArray());put(o,"records",new JSONArray());return o;}
    public static void put(JSONObject o,String key,Object value){try{o.put(key,value);}catch(JSONException e){throw new IllegalStateException(e);}}
    public JSONArray children(){return state.optJSONArray("children");}
    public JSONArray records(){return state.optJSONArray("records");}
    public JSONObject child(String id){for(int i=0;i<children().length();i++){JSONObject c=children().optJSONObject(i);if(c.optString("id").equals(id))return c;}return null;}
    public JSONObject selected(){JSONObject c=child(state.optString("selected"));return c!=null?c:children().optJSONObject(0);}
    public String selectedId(){JSONObject c=selected();return c==null?"":c.optString("id");}
    public void select(String id){if(child(id)!=null){put(state,"selected",id);save();}}
    public String add(String name,int age){
        if(children().length()>=20)throw new IllegalArgumentException("Maximum 20 learners");
        JSONObject c=new JSONObject();String id=UUID.randomUUID().toString();put(c,"id",id);update(c,name,age);children().put(c);put(state,"selected",id);save();return id;
    }
    public void edit(String id,String name,int age){JSONObject c=child(id);if(c==null)throw new IllegalArgumentException("Unknown learner");update(c,name,age);save();}
    private static void update(JSONObject c,String name,int age){if(name==null||name.trim().isEmpty()||name.trim().length()>40||age<7||age>11)throw new IllegalArgumentException("Invalid learner details");put(c,"name",name.trim());put(c,"age",age);}
    public void delete(String id){
        JSONArray children=new JSONArray(),records=new JSONArray();
        for(int i=0;i<children().length();i++)if(!children().optJSONObject(i).optString("id").equals(id))children.put(children().optJSONObject(i));
        for(int i=0;i<records().length();i++)if(!records().optJSONObject(i).optString("child").equals(id))records.put(records().optJSONObject(i));
        put(state,"children",children);put(state,"records",records);if(state.optString("selected").equals(id))put(state,"selected","");save();
    }
    public boolean complete(PracticeRound round){
        if(!round.valid()||!round.finished)throw new IllegalArgumentException("Incomplete activity");
        JSONObject c=child(round.child);if(c==null||c.optString("lastRound").equals(round.id))return false;
        for(int i=0;i<records().length();i++)if(records().optJSONObject(i).optString("id").equals(round.id))return false;
        put(c,"stars",c.optInt("stars")+5);put(c,"rounds",c.optInt("rounds")+1);put(c,"lastRound",round.id);
        if(round.mode==1||round.mode==2){put(c,"independent",c.optInt("independent")+round.independent);put(c,"questions",c.optInt("questions")+5);}
        long now=System.currentTimeMillis();String today=ProgressStats.date(now);
        JSONArray dates=c.optJSONArray("dates");if(dates==null)dates=new JSONArray();boolean found=false;for(int i=0;i<dates.length();i++)if(today.equals(dates.optString(i)))found=true;if(!found)dates.put(today);put(c,"dates",dates);
        JSONObject r=new JSONObject();put(r,"id",round.id);put(r,"child",round.child);put(r,"level",round.level);put(r,"mode",round.mode);put(r,"independent",round.independent);put(r,"date",today);put(r,"time",now);records().put(r);
        if(records().length()>500){JSONArray recent=new JSONArray();for(int i=records().length()-500;i<records().length();i++)recent.put(records().optJSONObject(i));put(state,"records",recent);}
        save();return true;
    }
    public Set<String> dates(JSONObject child){Set<String> result=new HashSet<>();JSONArray dates=child.optJSONArray("dates");if(dates!=null)for(int i=0;i<dates.length();i++)result.add(dates.optString(i));return result;}
    public String snapshot(){return state.toString();}
    public void restore(String json) throws JSONException {JSONObject replacement=validate(new JSONObject(json));state=replacement;save();}
    private static JSONObject validate(JSONObject o) throws JSONException {
        if(o.optInt("version")!=2||!(o.opt("children") instanceof JSONArray)||!(o.opt("records") instanceof JSONArray))throw new JSONException("Unsupported data format");
        JSONArray children=o.getJSONArray("children"),records=o.getJSONArray("records");if(children.length()>20||records.length()>500)throw new JSONException("Data limit exceeded");
        Set<String> ids=new HashSet<>();
        for(int i=0;i<children.length();i++){JSONObject c=children.getJSONObject(i);String id=c.getString("id");if(id.isEmpty()||!ids.add(id)||c.optInt("rounds")<0||c.optInt("stars")<0)throw new JSONException("Invalid learner");update(c,c.getString("name"),c.getInt("age"));}
        for(int i=0;i<records.length();i++){JSONObject r=records.getJSONObject(i);if(!ids.contains(r.getString("child"))||r.getInt("mode")<0||r.getInt("mode")>3||r.getInt("level")<0||r.getInt("level")>2)throw new JSONException("Invalid result");}
        return o;
    }
    public void importChild(String id,String name,int age){if(child(id)!=null)return;if(children().length()>=20)return;JSONObject c=new JSONObject();put(c,"id",id);update(c,name,age);children().put(c);save();}
    public void importGarden(SharedPreferences original,String defaultName){
        if(prefs.getBoolean("gardenImported",false))return;
        if(children().length()==0&&original.getInt("rounds",0)>0){String id=add(defaultName,7);JSONObject c=child(id);put(c,"rounds",Math.max(0,original.getInt("rounds",0)));put(c,"stars",Math.max(0,original.getInt("stars",0)));save();}
        prefs.edit().putBoolean("gardenImported",true).apply();
    }
    private void save(){prefs.edit().putString("state",state.toString()).apply();}
}
