package com.silkysys.warehouse.sharedcode.data.remote

import com.silkysys.warehouse.sharedcode.domain.addstock.AddStockRequest
import com.silkysys.warehouse.sharedcode.data.autentcation.forgetPasswordVerifyCode.dto.VerifyCodeRequest
import com.silkysys.warehouse.sharedcode.data.autentcation.login.dto.request.LoginRequest
import com.silkysys.warehouse.sharedcode.data.autentcation.updateForgetPassword.dto.UpdateForgetPasswordRequest
import com.silkysys.warehouse.sharedcode.data.autentcation.sendemail.dto.SendEmailRequest
import com.silkysys.warehouse.sharedcode.data.packing.addPack.dto.AddPackRequest
import com.silkysys.warehouse.data.picking.startPickUpdateDialogQuantity.dto.UpdateQuantityRequest
import com.silkysys.warehouse.sharedcode.data.picking.updatePickStatus.dto.UpdatePickStatusRequest
import com.silkysys.warehouse.data.putAwayModule.deletePinLocation.dto.DeleteLocationRequest
import com.silkysys.warehouse.sharedcode.data.putAwayModule.putAwayProductDetails.dto.PutAwayProductDetailsRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType



class ApiService(private val client: HttpClient) {

    // ── Auth ──────────────────────────────────────────────────────────────────

    suspend fun login(request: LoginRequest): HttpResponse =
        client.post("api/v1/login") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }

    suspend fun logout(): HttpResponse =
        client.post("api/v1/logout/")

    suspend fun forgotPassword(request: SendEmailRequest): HttpResponse =
        client.post("api/v1/auth/forgot-password") {
            contentType(ContentType.Application.Json)
            url {
                parameters.append("email", request.email)
            }
        }

    suspend fun verifyEmail(request: VerifyCodeRequest): HttpResponse =
        client.post("api/v1/auth/verify-otp") {
            contentType(ContentType.Application.Json)
            url {
                parameters.append("email", request.email)
                parameters.append("otp", request.code)
            }
        }

    suspend fun updateForgetPassword(request: UpdateForgetPasswordRequest): HttpResponse =
        client.post("api/v1/auth/reset-password") {
            contentType(ContentType.Application.Json)
            url {
                parameters.append("email", request.email)
                parameters.append("reset_token", request.reset_token)
                parameters.append("password", request.new_password)
                parameters.append("password_confirmation", request.confirm_new_password)
            }
        }

    // ── User ──────────────────────────────────────────────────────────────────

    suspend fun user(): HttpResponse =
        client.get("api/v1/accounting/api/warehouse/user")

    // ── Inventory ─────────────────────────────────────────────────────────────

    suspend fun addStock(request: AddStockRequest): HttpResponse =
        client.post("api/v1/accounting/api/web/v2/inventory/inbound-stock") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }

    suspend fun getProductInfo(itemBarcode: String): HttpResponse =
        client.get("api/v1/accounting/api/web/v2/inventory/items/scan/$itemBarcode")

    // ── Put Away ──────────────────────────────────────────────────────────────

    suspend fun putAwayScan(itemBarcode: String): HttpResponse =
        client.get("api/v1/accounting/api/warehouse/item-scan/$itemBarcode")

    suspend fun assignProductToBinLocation(request: PutAwayProductDetailsRequest): HttpResponse =
        client.post("api/v1/accounting/api/warehouse/item/assign-to-bin") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }

    suspend fun deleteLocation(
        companyId: String,
        request: DeleteLocationRequest
    ): HttpResponse =
        client.delete("$companyId/api/warehouse/item/unassign-bin") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }

    // ── Picking ───────────────────────────────────────────────────────────────

    suspend fun getPickWaitingAndStarted(): HttpResponse =
        client.get("api/v1/accounting/api/warehouse/fulfillments/pickingAndWaiting")

    suspend fun updatePickStatus(request: UpdatePickStatusRequest): HttpResponse =
        client.post("api/v1/accounting/api/warehouse/fulfillments/batch-status-update") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }

    suspend fun startPick(fulfillmentId: Int): HttpResponse =
        client.post("api/v1/accounting/api/warehouse/fulfillments/status/picking/$fulfillmentId")
            .body()

    suspend fun getStartedList(fulfillmentId: Int): HttpResponse =
        client.get("api/v1/accounting/api/warehouse/fulfillment/picklist/$fulfillmentId")

    suspend fun updateQuantity(
        fulfillmentId: Int,
        itemCode: String
    ): HttpResponse =
        client.post("api/v1/accounting/api/warehouse/fulfillment/item/increment-quantity") {
            parameter("fulfillment_id", fulfillmentId)
            parameter("item_code", itemCode)
        }.body()

    suspend fun updateQuantityFromDialog(
        fulfillmentId: Int,
        request: UpdateQuantityRequest
    ): HttpResponse =
        client.post("api/v1/accounting/api/warehouse/fulfillment-picking/start/$fulfillmentId") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()

    // ── Packing ───────────────────────────────────────────────────────────────

    suspend fun getPackList(status: String): HttpResponse =
        client.get("api/v1/accounting/api/warehouse/fulfillments") {
            parameter("status", status)
        }

    suspend fun getPackDetails(fulfillmentId: Int): HttpResponse =
        client.get("api/v1/accounting/api/warehouse/fulfillment/pack-list/$fulfillmentId")

    suspend fun finishPack(fulfillmentId: Int): HttpResponse =
        client.post("api/v1/accounting/api/warehouse/fulfillment-packing/finish/$fulfillmentId")

    suspend fun getPackItems(fulfillmentId: Int): HttpResponse =
        client.get("api/v1/accounting/api/warehouse/fulfillment-packing/items/$fulfillmentId")
            .body()

    suspend fun addPackItem(
        fulfillmentId: Int,
        request: AddPackRequest
    ): HttpResponse =
        client.post("api/v1/accounting/api/warehouse/fulfillment-packing/submit/$fulfillmentId") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
}
