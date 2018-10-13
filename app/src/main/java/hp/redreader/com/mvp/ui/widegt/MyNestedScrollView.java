package hp.redreader.com.mvp.ui.widegt;

import android.content.Context;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;

/**
 * 类名：    RedReader
 * 类描述：  系统的滚动监听只能api23以上用，这为了兼容
 * 创建人：  hp
 * 创建时间：2018/9/23/023 21:53
 * 修改人：  hp
 * 修改时间：2018/9/23/023 21:53
 * 修改备注：
 */
public class MyNestedScrollView extends NestedScrollView {

    private ScrollInterface scrollInterface;

    /**
     * 定义滑动接口
     */
    public interface ScrollInterface {
        void onScrollChange(int scrollX, int scrollY, int oldScrollX, int oldScrollY);
    }

    public MyNestedScrollView(Context context) {
        super(context);
    }

    public MyNestedScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyNestedScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        if (scrollInterface != null) {
            scrollInterface.onScrollChange(l, t, oldl, oldt);
        }
        super.onScrollChanged(l, t, oldl, oldt);
    }

    public void setOnScrollChangeListener(ScrollInterface t) {
        this.scrollInterface = t;
    }
}