/*
 * Copyright (C) 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.cupcake.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/** Mendeklasrasikan data yang akan digunakan yang tidak dapat dibuat*/
private const val PRICE_PER_CUPCAKE = 2.00

private const val PRICE_FOR_SAME_DAY_PICKUP = 3.00


class OrderViewModel : ViewModel() {

    // Banyak cupcakes dalam pesanan ini
    private val _quantity = MutableLiveData<Int>()
    val quantity: LiveData<Int> = _quantity

    // Cupcake flavor for this order
    private val _flavor = MutableLiveData<String>()
    val flavor: LiveData<String> = _flavor

    // List pilihan tanggal
    val dateOptions: List<String> = getPickupOptions()


    private val _date = MutableLiveData<String>()
    val date: LiveData<String> = _date


    private val _price = MutableLiveData<Double>()
    val price: LiveData<String> = Transformations.map(_price) {
        // Format harga ke dalam mata uang lokal dan kembalikan ini sebagai LiveData<String>
        NumberFormat.getCurrencyInstance().format(it)
    }

    init {
        resetOrder()
    }

    /**
     * Atur jumlah cupcakes untuk pesanan ini.
     *
     * @param numberCupcake sesuai pesanan
     */
    fun setQuantity(numberCupcakes: Int) {
        _quantity.value = numberCupcakes
        updatePrice()
    }

    /**
     * Atur rasa cupcakes untuk pesanan ini. Hanya 1 rasa yang dapat dipilih untuk seluruh pesanan.
     *
     * @param yang diinginkan Flavor adalah rasa cupcake sebagai string
     */
    fun setFlavor(desiredFlavor: String) {
        _flavor.value = desiredFlavor
    }

    /**
     * Tetapkan tanggal pengambilan untuk pesanan ini.
     *
     * @param pickupDate adalah tanggal pengambilan sebagai string
     */
    fun setDate(pickupDate: String) {
        _date.value = pickupDate
        updatePrice()
    }

    /**
     * Mengembalikan nilai true jika rasa belum dipilih untuk pesanan. Mengembalikan false sebaliknya.
     */
    fun hasNoFlavorSet(): Boolean {
        return _flavor.value.isNullOrEmpty()
    }

    /**
     * Set ulang  pesanan dengan mengunakan nilai default untuk quantitas, rasa, tanggal, and harga.
     */
    fun resetOrder() {
        _quantity.value = 0
        _flavor.value = ""
        _date.value = dateOptions[0]
        _price.value = 0.0
    }

    /**
     *  Memperbarui harga berdasarkan detail pesanan.
     */
    private fun updatePrice() {
        var calculatedPrice = (quantity.value ?: 0) * PRICE_PER_CUPCAKE
        // jika menyeleksi opsi pertama yaitu tanggal hari ini, tambahkan biaya tambahan
        if (dateOptions[0] == _date.value) {
            calculatedPrice += PRICE_FOR_SAME_DAY_PICKUP
        }
        _price.value = calculatedPrice
    }

    /**
     *  Mengembalikan list tanggal dari opsi tanggal yang dimulai dari tanggal saat ini dan ada 3 tanggal
     */
    private fun getPickupOptions(): List<String> {
        val options = mutableListOf<String>()
        val formatter = SimpleDateFormat("E MMM d", Locale.getDefault())
        val calendar = Calendar.getInstance()
        repeat(4) {
            options.add(formatter.format(calendar.time))
            calendar.add(Calendar.DATE, 1)
        }
        return options
    }
}