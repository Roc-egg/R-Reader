package hp.redreader.com.di.component

import dagger.Component
import com.jess.arms.di.component.AppComponent

import hp.redreader.com.di.module.BookListModule

import com.jess.arms.di.scope.FragmentScope
import hp.redreader.com.mvp.ui.fragment.wanChild.BookListFragment

@FragmentScope
@Component(modules = arrayOf(BookListModule::class), dependencies = arrayOf(AppComponent::class))
interface BookListComponent {
    fun inject(fragment: BookListFragment)
}
