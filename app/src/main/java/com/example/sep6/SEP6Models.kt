package com.example.sep6

import java.io.Serializable

data class DepositData(val type:String, val url:String?, val identifier:String, val dimensions:Dimension)

data class Dimension(val width:String, val height:String)

data class DepositSEP6(var asset:String?,
    val how:String, val fee_fixed:String,
                       val fee_percent:String, val eta:String,
                       val min_amount:String, val max_amount:String) : Serializable

data class WithdrawData(val type:String, val url:String, val identifier:String)