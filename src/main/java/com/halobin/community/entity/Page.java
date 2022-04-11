package com.halobin.community.entity;

public class Page {
    //当前页
    private int cur  = 1;
    //页上限
    private int limit =10;
    //总数
    private int count;
    //路径
    private String path;

    public int getCur() {
        return cur;
    }

    public void setCur(int cur) {
        if(cur >= 1){
            this.cur = cur;
        }
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        if(limit >= 1 && limit <=100){
            this.limit = limit;
        }
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    /**
     * 获取总页数
     *
     * @return
     */
    public int getTotalPage(){
        return count / limit + (count % limit != 0 ? 1 : 0);
    }

    /**
     * 获取页偏移
     *
     * @return
     */
    public int getOffset(){
        return (cur - 1) * limit;
    }

    /**
     * 获取起始页
     *
     * @return
     */
    public int getFromPage(){
        int from = cur - 2;
        return from >= 1 ? from : 1;
    }

    /**
     * 获取终止页
     *
     * @return
     */
    public int getToPage(){
        int to = cur + 2;
        return to <= getTotalPage() ? to : getTotalPage();
    }
}
