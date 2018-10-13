package hp.redreader.com.di.module

import com.jess.arms.di.scope.FragmentScope

import dagger.Module
import dagger.Provides

import hp.redreader.com.mvp.contract.JokeContract
import hp.redreader.com.mvp.model.JokeModel
import hp.redreader.com.mvp.model.entity.book.BookBean
import hp.redreader.com.mvp.model.entity.wanandroid.DuanZiBean
import hp.redreader.com.mvp.ui.adapter.JokeAdapter
import hp.redreader.com.mvp.ui.adapter.WanBookAdapter


@Module
//构建JokeModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
class JokeModule(private val view: JokeContract.View) {
    @FragmentScope
    @Provides
    fun provideJokeView(): JokeContract.View {
        return this.view
    }

    @FragmentScope
    @Provides
    fun provideJokeModel(model: JokeModel): JokeContract.Model {
        return model
    }


    @FragmentScope
    @Provides
    internal fun provideDatas() = mutableListOf<DuanZiBean>()

    @FragmentScope
    @Provides
    internal fun provideAdapter(datas: MutableList<DuanZiBean>) = JokeAdapter(datas)
}
