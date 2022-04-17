package com.halobin.community.entity;

import java.util.HashMap;
import java.util.Map;

public class TrieNode {

    private boolean isEnd = false;

    Map<Character, TrieNode> chileNodes = new HashMap<>();

    public boolean isEnd() {
        return isEnd;
    }

    public void setEnd(boolean end) {
        isEnd = end;
    }

    public void addChildNode(Character c, TrieNode tr){
        chileNodes.put(c, tr);
    }

    public TrieNode getChildNode(Character c){
        return chileNodes.get(c);
    }
}
