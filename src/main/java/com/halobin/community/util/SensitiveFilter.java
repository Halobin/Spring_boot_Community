package com.halobin.community.util;

import com.halobin.community.entity.TrieNode;
import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@Component
public class SensitiveFilter {

    private static final String REPLACEMENT = "***";
    private TrieNode rootNode = new TrieNode();

    @PostConstruct
    public void init(){
        try(
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        ) {
            String keyword;
            while((keyword = reader.readLine()) != null){
                this.addKeyWord(keyword);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addKeyWord(String keyword){
        TrieNode tmpNode = rootNode;
        for(int i = 0; i < keyword.length(); i++){
            char c = keyword.charAt(i);
            TrieNode childNode = tmpNode.getChildNode(c);

            if(childNode == null){
                childNode = new TrieNode();
                tmpNode.addChildNode(c, childNode);
            }

            tmpNode = childNode;

            if(i == keyword.length() - 1){
                tmpNode.setEnd(true);
            }
        }
    }

    public String filterText(String text){
        if(StringUtils.isBlank(text)){
            return null;
        }

        StringBuilder sb = new StringBuilder();
        TrieNode tmpNode = rootNode;
        int sta = 0, pos = 0;

        while(sta < text.length()){

            if(pos < text.length()){
                char c = text.charAt(pos);
                if(isSymbol(c)){
                    if(tmpNode == rootNode){
                        sb.append(c);
                        ++sta;
                    }
                    ++pos;
                    continue;
                }

                tmpNode = tmpNode.getChildNode(c);
                if(tmpNode == null){
                    try {
                        sb.append(text.charAt(sta));
                    } catch (Exception e) {
                        System.out.println(sta);
                    }
                    pos = ++sta;
                    tmpNode = rootNode;
                }else if(tmpNode.isEnd()){
                    sb.append(REPLACEMENT);
                    sta = ++pos;
                    tmpNode = rootNode;
                }else{
                    ++pos;
                }
            }else{
                sb.append(text.charAt(sta));
                pos = ++sta;
                tmpNode = rootNode;
            }
        }
        return sb.toString();
    }

    private boolean isSymbol(Character c){
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
    }
}
