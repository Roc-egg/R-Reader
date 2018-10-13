package hp.redreader.com.di.component

import dagger.Component
import com.jess.arms.di.component.AppComponent

import hp.redreader.com.di.module.GankModule

import com.jess.arms.di.scope.FragmentScope
import hp.redreader.com.mvp.ui.fragment.GankFragment

@FragmentScope
@Component(modules = arrayOf(GankModule::class), dependencies = arrayOf(AppComponent::class))
interface GankComponent {
    fun inject(fragment: GankFragment)
}
