package hp.redreader.com.di.module

import android.support.v7.widget.GridLayoutManager
import com.jess.arms.di.scope.FragmentScope

import dagger.Module
import dagger.Provides

import hp.redreader.com.mvp.contract.BookListContract
import hp.redreader.com.mvp.model.BookListModel
import hp.redreader.com.mvp.model.entity.book.BookBean
import hp.redreader.com.mvp.ui.adapter.WanBookAdapter


@Module
//构建BookListModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
class BookListModule(private val view: BookListContract.View) {
    @FragmentScope
    @Provides
    fun provideBookListView(): BookListContract.View {
        return this.view
    }

    @FragmentScope
    @Provides
    fun provideBookListModel(model: BookListModel): BookListContract.Model {
        return model
    }
    @FragmentScope
    @Provides
    internal fun provideLayoutManager() = GridLayoutManager(view.getActivitys(), 3)

    @FragmentScope
    @Provides
    internal fun provideDatas() = mutableListOf<BookBean.BooksBean>()

    @FragmentScope
    @Provides
    internal fun provideAdapter(datas: MutableList<BookBean.BooksBean>) = WanBookAdapter(datas)

}
