package hp.redreader.com.di.component

import dagger.Component
import com.jess.arms.di.component.AppComponent

import hp.redreader.com.di.module.WanModule

import com.jess.arms.di.scope.FragmentScope
import hp.redreader.com.mvp.ui.fragment.WanFragment

@FragmentScope
@Component(modules = arrayOf(WanModule::class), dependencies = arrayOf(AppComponent::class))
interface WanComponent {
    fun inject(fragment: WanFragment)
}
