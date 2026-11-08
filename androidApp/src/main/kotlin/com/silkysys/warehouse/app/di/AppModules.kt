package com.silkysys.warehouse.app.di


import com.silkysys.warehouse.app.addStock.AddStockViewModel
import com.silkysys.warehouse.app.security.RecaptchaManager
import com.silkysys.warehouse.app.autentcation.forgetPassword.ForgetPasswordViewModel
import com.silkysys.warehouse.app.autentcation.forgetPasswordVerifyCode.VerifyCodeViewModel
import com.silkysys.warehouse.app.autentcation.login.LoginViewModel
import com.silkysys.warehouse.app.autentcation.main.MainViewModel
import com.silkysys.warehouse.app.autentcation.profile.ProfileViewModel
import com.silkysys.warehouse.app.autentcation.forgetPasswordSendEmail.SendEmailViewmodel
import com.silkysys.warehouse.app.network.networkModule
import com.silkysys.warehouse.app.packing.addPack.AddPackViewModel
import com.silkysys.warehouse.app.packing.pack.PackViewModel
import com.silkysys.warehouse.app.packing.packDetails.PackDetailsViewModel
import com.silkysys.warehouse.app.picking.pick.PickViewModel
import com.silkysys.warehouse.app.picking.startPick.StartPickViewModel
import com.silkysys.warehouse.app.putAway.putAway.PutAwayViewModel
import com.silkysys.warehouse.app.putAway.putAwayProductDetails.PutAwayProductDetailsViewModel
import com.silkysys.warehouse.app.ui.utility.SavePref
import com.silkysys.warehouse.sharedcode.data.autentcation.forgetPasswordVerifyCode.SendVerifyCodeRepoImpl
import com.silkysys.warehouse.sharedcode.data.autentcation.logOut.LogOutRepoImpl
import com.silkysys.warehouse.sharedcode.data.autentcation.login.LoginRepoImpl
import com.silkysys.warehouse.sharedcode.data.autentcation.updateForgetPassword.UpdateForgetPasswordRepoImpl
import com.silkysys.warehouse.sharedcode.data.autentcation.sendemail.SendEmailRepoImpl
import com.silkysys.warehouse.sharedcode.data.autentcation.user.UserRepoImpl
import com.silkysys.warehouse.sharedcode.data.autentcation.warehouses.WareHousesRepoImpl
import com.silkysys.warehouse.sharedcode.data.packing.addPack.AddPackRepoImpl
import com.silkysys.warehouse.sharedcode.data.packing.finishPack.FinishPackRepoImpl
import com.silkysys.warehouse.sharedcode.data.packing.packDetails.PackDetailsRepoImpl
import com.silkysys.warehouse.sharedcode.data.packing.packItems.PackItemsRepoImpl
import com.silkysys.warehouse.sharedcode.data.packing.packList.PackListRepoImpl
import com.silkysys.warehouse.sharedcode.data.picking.pick.PickWaitingAndStartedRepoImpl
import com.silkysys.warehouse.sharedcode.data.picking.startPick.StartPickRepoImpl
import com.silkysys.warehouse.sharedcode.data.picking.startPickUpdateDialogQuantity.UpdateDialogQuantityRepoImpl
import com.silkysys.warehouse.sharedcode.data.picking.startPickUpdateQuantity.UpdateQuantityRepoImpl
import com.silkysys.warehouse.sharedcode.data.picking.updatePickStatus.UpdatePickStatusRpoImpl
import com.silkysys.warehouse.sharedcode.data.putAwayModule.deletePinLocation.DeleteLocationRepoImpl
import com.silkysys.warehouse.sharedcode.data.putAwayModule.putAway.PutAwayRepoImpl
import com.silkysys.warehouse.sharedcode.data.putAwayModule.putAwayProductDetails.PutAwayProductDetailsRepoImpl
import com.silkysys.warehouse.sharedcode.data.startedItems.StartedItemsRepoImpl
import com.silkysys.warehouse.sharedcode.domain.addstock.AddStockRepo
import com.silkysys.warehouse.sharedcode.domain.addstock.AddStockRepoImpl
import com.silkysys.warehouse.sharedcode.domain.addstock.AddStockUseCase
import com.silkysys.warehouse.sharedcode.domain.autentcation.forgetPasswordVerifyCode.VerifyCodeRepo
import com.silkysys.warehouse.sharedcode.domain.autentcation.forgetPasswordVerifyCode.useCase.VerifyCodeUseCase
import com.silkysys.warehouse.sharedcode.domain.autentcation.logOut.LogOutRepo
import com.silkysys.warehouse.sharedcode.domain.autentcation.logOut.useCase.LogOutUseCase
import com.silkysys.warehouse.sharedcode.domain.autentcation.login.LoginRepo
import com.silkysys.warehouse.sharedcode.domain.autentcation.login.useCase.LoginUseCase
import com.silkysys.warehouse.sharedcode.domain.autentcation.updateForgetPassword.UpdateForgetPasswordRepo
import com.silkysys.warehouse.sharedcode.domain.autentcation.updateForgetPassword.useCase.UpdateForgetPasswordUseCase
import com.silkysys.warehouse.sharedcode.domain.autentcation.sendemail.SendEmailRepo
import com.silkysys.warehouse.sharedcode.domain.autentcation.sendemail.useCase.SendEmailUseCase
import com.silkysys.warehouse.sharedcode.domain.autentcation.user.UserRepo
import com.silkysys.warehouse.sharedcode.domain.autentcation.user.useCase.UserUseCase
import com.silkysys.warehouse.sharedcode.domain.autentcation.warehouses.WareHousesRepo
import com.silkysys.warehouse.sharedcode.domain.autentcation.warehouses.useCase.WareHousesUseCase
import com.silkysys.warehouse.sharedcode.domain.packing.addPackItem.AddPackRepo
import com.silkysys.warehouse.sharedcode.domain.packing.addPackItem.useCase.AddPackUseCase
import com.silkysys.warehouse.sharedcode.domain.packing.finishPack.FinishPackRepo
import com.silkysys.warehouse.sharedcode.domain.packing.finishPack.useCase.FinishPackUseCase
import com.silkysys.warehouse.sharedcode.domain.packing.packDetails.PackDetailsRepo
import com.silkysys.warehouse.sharedcode.domain.packing.packDetails.useCase.PackDetailsUseCase
import com.silkysys.warehouse.sharedcode.domain.packing.packItems.PackItemsRepo
import com.silkysys.warehouse.sharedcode.domain.packing.packItems.useCase.PackItemsUseCase
import com.silkysys.warehouse.sharedcode.domain.packing.packList.PackListRepo
import com.silkysys.warehouse.sharedcode.domain.packing.packList.useCase.PackListUseCase
import com.silkysys.warehouse.sharedcode.domain.picking.pick.PickWaitingAndStartedRepo
import com.silkysys.warehouse.sharedcode.domain.picking.pick.useCase.PickWaitingAndStartedUseCase
import com.silkysys.warehouse.sharedcode.domain.picking.startPick.StartPickRepo
import com.silkysys.warehouse.sharedcode.domain.picking.startPick.useCase.StartPickUseCase
import com.silkysys.warehouse.sharedcode.domain.picking.startPickUpdateDialogQuantity.UpdateDialogQuantityRepo
import com.silkysys.warehouse.sharedcode.domain.picking.startPickUpdateDialogQuantity.useCase.UpdateDialogQuantityUseCase
import com.silkysys.warehouse.sharedcode.domain.picking.startPickUpdateQuantity.UpdateQuantityRepo
import com.silkysys.warehouse.sharedcode.domain.picking.startPickUpdateQuantity.useCase.UpdateQuantityUseCase
import com.silkysys.warehouse.sharedcode.domain.picking.startedItems.StartedItemsRepo
import com.silkysys.warehouse.sharedcode.domain.picking.startedItems.useCase.StartedItemsUseCase
import com.silkysys.warehouse.sharedcode.domain.picking.updatePickStatus.UpdatePickStatusRepo
import com.silkysys.warehouse.sharedcode.domain.picking.updatePickStatus.useCase.UpdatePickStatusUseCase
import com.silkysys.warehouse.sharedcode.domain.putAwayModule.deletePinLocation.DeleteLocationRepo
import com.silkysys.warehouse.sharedcode.domain.putAwayModule.deletePinLocation.useCase.DeleteLocationUseCase
import com.silkysys.warehouse.sharedcode.domain.putAwayModule.putAway.PutAwayRepo
import com.silkysys.warehouse.sharedcode.domain.putAwayModule.putAway.useCase.PutAwayUseCase
import com.silkysys.warehouse.sharedcode.domain.putAwayModule.putAwayProductDetails.PutAwayProductDetailsRepo
import com.silkysys.warehouse.sharedcode.domain.putAwayModule.putAwayProductDetails.useCase.PutAwayProductDetailsUseCase
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val sharedPreferenceModule = module {
    single { SavePref(androidContext()) }
}

val deleteLocationModule = module {
    single<DeleteLocationRepo> { DeleteLocationRepoImpl(get()) }
    single { DeleteLocationUseCase(get()) }
}

val finishPackModule = module {
    single<FinishPackRepo> { FinishPackRepoImpl(get()) }
    single { FinishPackUseCase(get()) }
}

val startedItemsModule = module {
    single<StartedItemsRepo> { StartedItemsRepoImpl(get()) }
    single { StartedItemsUseCase(get()) }
}

val updatePasswordModule = module {
    single<SendEmailRepo> { SendEmailRepoImpl(get()) }
    single { SendEmailUseCase(get()) }
}

val logOutModule = module {
    single<LogOutRepo> { LogOutRepoImpl(get()) }
    single { LogOutUseCase(get()) }
}

val packItemsModule = module {
    single<PackItemsRepo> { PackItemsRepoImpl(get()) }
    single { PackItemsUseCase(get()) }
}

val packModule = module {
    single<PackListRepo> { PackListRepoImpl(get()) }
    single { PackListUseCase(get()) }
}

val updateDialogQuantityModule = module {
    single<UpdateDialogQuantityRepo> { UpdateDialogQuantityRepoImpl(get()) }
    single { UpdateDialogQuantityUseCase(get()) }
}

val addPackItem = module {
    single<AddPackRepo> { AddPackRepoImpl(get()) }
    single { AddPackUseCase(get()) }
}

val loginModule = module {
    single<LoginRepo> { LoginRepoImpl(get()) }
    single { LoginUseCase(get()) }
}

val putAwayModule = module {
    single<PutAwayRepo> { PutAwayRepoImpl(get()) }
    single { PutAwayUseCase(get()) }
}

val putAwayProductDetailsModule = module {
    single<PutAwayProductDetailsRepo> { PutAwayProductDetailsRepoImpl(get()) }
    single { PutAwayProductDetailsUseCase(get()) }
}

val userModule = module {
    single<UserRepo> { UserRepoImpl(get()) }
    single { UserUseCase(get()) }
}

val wareHousesModule = module {
    single<WareHousesRepo> { WareHousesRepoImpl(get()) }
    single { WareHousesUseCase(get()) }
}

val forgetPasswordVerifyCodeModule = module {
    single<VerifyCodeRepo> { SendVerifyCodeRepoImpl(get()) }
    single { VerifyCodeUseCase(get()) }
}

val updatePickStatusModule = module {
    single<UpdatePickStatusRepo> { UpdatePickStatusRpoImpl(get()) }
    single { UpdatePickStatusUseCase(get()) }
}

val pickWaitingAndStartedModule = module {
    single<PickWaitingAndStartedRepo> { PickWaitingAndStartedRepoImpl(get()) }
    single { PickWaitingAndStartedUseCase(get()) }
}

val updateQuantityModule = module {
    single<UpdateQuantityRepo> { UpdateQuantityRepoImpl(get()) }
    single { UpdateQuantityUseCase(get()) }
}

val updateForgetPasswordModule = module {
    single<UpdateForgetPasswordRepo> { UpdateForgetPasswordRepoImpl(get()) }
    single { UpdateForgetPasswordUseCase(get()) }
}

val startPickModule = module {
    single<StartPickRepo> { StartPickRepoImpl(get()) }
    single { StartPickUseCase(get()) }
}

val packDetailsModule = module {
    single<PackDetailsRepo> { PackDetailsRepoImpl(get()) }
    single { PackDetailsUseCase(get()) }
}

val addStockModule = module {
    single<AddStockRepo> { AddStockRepoImpl(get()) }
    single { AddStockUseCase(get()) }
}

val appModule = module {
    includes(
        networkModule,
        sharedPreferenceModule,
        deleteLocationModule,
        finishPackModule,
        startedItemsModule,
        updatePasswordModule,
        logOutModule,
        packItemsModule,
        packModule,
        updateDialogQuantityModule,
        addPackItem,
        loginModule,
        putAwayModule,
        putAwayProductDetailsModule,
        userModule,
        wareHousesModule,
        forgetPasswordVerifyCodeModule,
        updatePickStatusModule,
        pickWaitingAndStartedModule,
        updateQuantityModule,
        updateForgetPasswordModule,
        startPickModule,
        packDetailsModule,
        addStockModule
    )

    single { RecaptchaManager(androidContext()) }

    viewModel { MainViewModel(get(), get()) }
    viewModel { LoginViewModel(get(), get()) }
    viewModel { PutAwayViewModel(get()) }
    viewModel { ProfileViewModel(get()) }
    viewModel { AddStockViewModel(get()) }
    viewModel { PutAwayProductDetailsViewModel(get(), get()) }
    viewModel { SendEmailViewmodel(get()) }
    viewModel { StartPickViewModel(get(), get(), get(), get()) }
    viewModel { PackViewModel(get()) }
    viewModel { ForgetPasswordViewModel(get()) }
    viewModel { PackDetailsViewModel(get(), get()) }
    viewModel { AddPackViewModel(get(), get()) }
    viewModel { PickViewModel(get(), get()) }
    viewModel { VerifyCodeViewModel(get()) }
}
