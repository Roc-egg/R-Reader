package hp.redreader.com.di.component

import dagger.Component
import com.jess.arms.di.component.AppComponent

import hp.redreader.com.di.module.EverydayModule

import com.jess.arms.di.scope.FragmentScope
import hp.redreader.com.mvp.ui.fragment.gankChild.EverydayFragment

@FragmentScope
@Component(modules = arrayOf(EverydayModule::class), dependencies = arrayOf(AppComponent::class))
interface EverydayComponent {
    fun inject(fragment: EverydayFragment)
}
