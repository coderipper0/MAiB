package com.coderipper.maib.usecases.main.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.coderipper.maib.R
import com.coderipper.maib.databinding.RecommendationsItemBinding
import com.coderipper.maib.models.domain.Product
import com.coderipper.maib.utils.DataBase

class RecommendationsAdapter(private val userId: Long, private val products: List<Product>):
    RecyclerView.Adapter<RecommendationsAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.recommendations_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(products[position])
    }

    override fun getItemCount() = products.size

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        private val binding = RecommendationsItemBinding.bind(view)

        fun bind(product: Product) {
            binding.run {
                titleText.text = product.name
                recommendationImage.setImageURI(product.images[0])

                addCartButton.addOnCheckedChangeListener { button, isChecked ->
                    if(isChecked)
                        DataBase.setToCart(userId, product.id)
                    else DataBase.removeFromCart(userId, product.id)
                }

                addWishlistButton.addOnCheckedChangeListener { button, isChecked ->
                    if(isChecked)
                        DataBase.setToWishlist(userId, product.id)
                    else DataBase.removeFromWishlist(userId, product.id)
                }
            }
        }
    }
}