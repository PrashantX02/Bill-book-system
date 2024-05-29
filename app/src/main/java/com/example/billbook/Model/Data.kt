package com.example.billbook.Model

data class ProductData(
    var productName: String,
    var sellingPrice: String,
    var productMrp: String,
    var productDiscount: String,
    var productStock: String
)

data class ProductModel(
    var ProductList: ArrayList<ProductData>
)


