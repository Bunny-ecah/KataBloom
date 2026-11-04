package com.example.myproject.learning;

import java.util.UUID;

/** A question is scored at most once. Hints/retries never remove participation stars. */
public final class PracticeRound {
    public String id=UUID.randomUUID().toString(), child="";
    public int level, mode, batch, index, independent;
    public boolean helped, solved, finished;
    public boolean solve(){if(solved||finished)return false;solved=true;if(!helped)independent++;return true;}
    public void help(){if(!solved&&!finished)helped=true;}
    public boolean next(){
        if(finished||!solved)return false;
        if(index==4){finished=true;return true;}
        index++;solved=false;helped=false;return true;
    }
    public boolean valid(){return !id.isEmpty()&&!child.isEmpty()&&level>=0&&level<3&&mode>=0&&mode<4&&batch>=0&&index>=0&&index<5&&independent>=0&&independent<=index+(solved?1:0)&&(!finished||(index==4&&solved));}
}
