package hp.redreader.com.di.module

import android.support.v7.widget.StaggeredGridLayoutManager
import com.jess.arms.di.scope.ActivityScope

import dagger.Module
import dagger.Provides

import hp.redreader.com.mvp.contract.DoubanTopContract
import hp.redreader.com.mvp.model.DoubanTopModel
import hp.redreader.com.mvp.model.entity.movie.SubjectsBean
import hp.redreader.com.mvp.ui.adapter.DouBanTopAdapter


@Module
//构建DoubanTopModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
class DoubanTopModule(private val view: DoubanTopContract.View) {
    @ActivityScope
    @Provides
    fun provideDoubanTopView(): DoubanTopContract.View {
        return this.view
    }

    @ActivityScope
    @Provides
    fun provideDoubanTopModel(model: DoubanTopModel): DoubanTopContract.Model {
        return model
    }

    @ActivityScope
    @Provides
    internal fun provideLayoutManager() = StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)

    @ActivityScope
    @Provides
    internal fun provideDatas() = mutableListOf<SubjectsBean>()

    @ActivityScope
    @Provides
    internal fun provideAdapter(datas: MutableList<SubjectsBean>) = DouBanTopAdapter(datas)
}
