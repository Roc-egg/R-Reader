package hp.redreader.com.di.module

import com.jess.arms.di.scope.FragmentScope

import dagger.Module
import dagger.Provides

import hp.redreader.com.mvp.contract.BannerContract
import hp.redreader.com.mvp.model.BannerModel
import hp.redreader.com.mvp.model.entity.wanandroid.HomeListBean
import hp.redreader.com.mvp.ui.adapter.WanAndroidAdapter


@Module
//构建BannerModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
class BannerModule(private val view: BannerContract.View) {
    @FragmentScope
    @Provides
    fun provideBannerView(): BannerContract.View {
        return this.view
    }

    @FragmentScope
    @Provides
    fun provideBannerModel(model: BannerModel): BannerContract.Model {
        return model
    }

    @FragmentScope
    @Provides
    internal fun provideDatas() = mutableListOf<HomeListBean.DatasBean>()

    @FragmentScope
    @Provides
    internal fun provideAdapter(datas: MutableList<HomeListBean.DatasBean>) = WanAndroidAdapter(datas)
}
