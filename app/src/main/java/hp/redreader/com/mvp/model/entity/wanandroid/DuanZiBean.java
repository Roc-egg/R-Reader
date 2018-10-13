package hp.redreader.com.mvp.model.entity.wanandroid;


import hp.redreader.com.mvp.model.entity.BaseResponse;

/**
 * @author jingbin
 * @data 2018/2/9
 * @Description 段子Bean
 */

public class DuanZiBean extends BaseResponse {

    private String name;
    private String avatarUrl;
    private long createTime;
    private String content;
    private String categoryName;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }


}
