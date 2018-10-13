package hp.redreader.com.di.module

import com.jess.arms.di.scope.FragmentScope

import dagger.Module
import dagger.Provides

import hp.redreader.com.mvp.contract.EverydayContract
import hp.redreader.com.mvp.model.EverydayModel
import hp.redreader.com.mvp.model.entity.gank.AndroidBean
import hp.redreader.com.mvp.ui.adapter.EverydayAdapter


@Module
//构建EverydayModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
class EverydayModule(private val view: EverydayContract.View) {
    @FragmentScope
    @Provides
    fun provideEverydayView(): EverydayContract.View {
        return this.view
    }

    @FragmentScope
    @Provides
    fun provideEverydayModel(model: EverydayModel): EverydayContract.Model {
        return model
    }

    @FragmentScope
    @Provides
    internal fun provideDatas() = mutableListOf<List<AndroidBean>>()

    @FragmentScope
    @Provides
    internal fun provideAdapter(datas: MutableList<List<AndroidBean>>) = EverydayAdapter(datas)
}
