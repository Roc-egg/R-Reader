package hp.redreader.com.mvp.model.entity.data;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 类名：    RedReader
 * 类描述：  用户信息User
 * 创建人：  hp
 * 创建时间：2018/9/20/020 19:13
 * 修改人：  hp
 * 修改时间：2018/9/20/020 19:13
 * 修改备注：
 */
@Entity
public class User {
    @Id
    private Long id;
    private String email;
    private String icon;
    private String password;
    private int type;
    private String username;
    @Generated(hash = 1125214697)
    public User(Long id, String email, String icon, String password, int type,
            String username) {
        this.id = id;
        this.email = email;
        this.icon = icon;
        this.password = password;
        this.type = type;
        this.username = username;
    }
    @Generated(hash = 586692638)
    public User() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getEmail() {
        return this.email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getIcon() {
        return this.icon;
    }
    public void setIcon(String icon) {
        this.icon = icon;
    }
    public String getPassword() {
        return this.password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public int getType() {
        return this.type;
    }
    public void setType(int type) {
        this.type = type;
    }
    public String getUsername() {
        return this.username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", icon='" + icon + '\'' +
                ", password='" + password + '\'' +
                ", type=" + type +
                ", username='" + username + '\'' +
                '}';
    }
}
