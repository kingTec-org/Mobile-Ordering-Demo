package com.ryanjames.swabergersmobilepos.feature.menuitemdetail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ryanjames.swabergersmobilepos.R
import com.ryanjames.swabergersmobilepos.databinding.RowItemSelectBinding
import com.ryanjames.swabergersmobilepos.domain.*

private const val ID_MEAL_OPTION = 1
private const val ID_PRODUCT_GROUP = 2
private const val ID_PRODUCT_GROUP_MODIFIER = 3

class MenuItemDetailAdapter(
    val product: Product,
    private val onClickRowListener: OnClickRowListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var selectedBundle: ProductBundle? = null
    private var productSelection = HashMap<ProductGroup, Product?>()
    private var productGroupModifierSelection = HashMap<Pair<Product, ModifierGroup>, ModifierInfo?>()

    private var data: List<RowDataHolder> = createRowDataHolders()

    fun setBundle(productBundle: ProductBundle?) {
        selectedBundle = productBundle
        notifyChange()
    }

    fun setProductSelection(selection: HashMap<ProductGroup, Product?>) {
        productSelection = selection
        notifyChange()
    }

    fun setProductGroupModifierSelection(selection: HashMap<Pair<Product, ModifierGroup>, ModifierInfo?>) {
        productGroupModifierSelection = selection
        notifyChange()
    }

    private fun notifyChange() {
        data = createRowDataHolders()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = RowItemSelectBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return when (viewType) {
            ID_MEAL_OPTION -> {
                RowSelectMealViewHolder(binding, onClickRowListener)
            }
            ID_PRODUCT_GROUP -> {
                RowSelectProductGroupViewHolder(binding, onClickRowListener)
            }
            else -> {
                RowSelectProductGroupModifierViewHolder(binding, onClickRowListener)
            }
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is RowSelectMealViewHolder -> {
                holder.bind(selectedBundle)
            }
            is RowSelectProductGroupViewHolder -> {
                val dataHolder = data[position] as RowDataHolder.RowProductGroupDataHolder
                val productGroup = dataHolder.productGroup
                holder.bind(productGroup, productSelection[productGroup])
            }
            is RowSelectProductGroupModifierViewHolder -> {
                val dataHolder = data[position] as RowDataHolder.RowProductGroupModifierDataHolder
                val product = dataHolder.product
                val modifierGroup = dataHolder.modifierGroup
                holder.bind(product, modifierGroup, productGroupModifierSelection[Pair(product, modifierGroup)])
            }
        }
    }

    override fun getItemViewType(position: Int): Int = data[position].itemViewType


    class RowSelectMealViewHolder(
        val binding: RowItemSelectBinding,
        private val onClickRowListener: OnClickRowListener
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(selectedBundle: ProductBundle?) {
            binding.tvHeader.setText(R.string.meal_options)
            if (selectedBundle == null) {
                binding.tvSubheader.setText(R.string.ala_carte)
            } else {
                binding.tvSubheader.text = selectedBundle.bundleName
            }

            binding.root.setOnClickListener {
                onClickRowListener.onClickRowMealOptions()
            }
        }

    }

    class RowSelectProductGroupViewHolder(
        val binding: RowItemSelectBinding,
        private val onClickRowListener: OnClickRowListener
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(productGroup: ProductGroup, product: Product?) {
            binding.tvHeader.text = "SELECT ${productGroup.productGroupName.toUpperCase()}"
            if (product == null) {
                binding.tvSubheader.setText(R.string.none_selected)
            } else {
                binding.tvSubheader.text = product.productName
            }

            binding.root.setOnClickListener {
                onClickRowListener.onClickRowProductGroup(productGroup)
            }
        }
    }

    class RowSelectProductGroupModifierViewHolder(
        val binding: RowItemSelectBinding,
        private val onClickRowListener: OnClickRowListener
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product, modifierGroup: ModifierGroup, modifierInfo: ModifierInfo?) {
            binding.tvHeader.text = "SELECT ${modifierGroup.modifierGroupName.toUpperCase()}"
            if (modifierInfo == null) {
                binding.tvSubheader.setText(R.string.none_selected)
            } else {
                binding.tvSubheader.text = "${product.productName} - ${modifierInfo.modifierName}"
            }

            binding.root.setOnClickListener {
                onClickRowListener.onClickRowProductGroupModifier(product, modifierGroup)
            }
        }

    }

    private fun createRowDataHolders(): List<RowDataHolder> {
        val list = mutableListOf<RowDataHolder>()
        if (product.bundles.isNotEmpty()) {
            list.add(RowDataHolder.RowSelectMealDataHolder())
        }

        for (modifierGroup in product.modifierGroups) {
            list.add(RowDataHolder.RowProductGroupModifierDataHolder(product, modifierGroup))
        }

        selectedBundle?.productGroups?.forEach { productGroup ->
            list.add(RowDataHolder.RowProductGroupDataHolder(productGroup))

            val productSelection = productSelection[productGroup]
            productSelection?.modifierGroups?.forEach { modifierGroup ->
                list.add(RowDataHolder.RowProductGroupModifierDataHolder(productSelection, modifierGroup))
            }

        }

        return list
    }

    private sealed class RowDataHolder {

        abstract val itemViewType: Int

        class RowSelectMealDataHolder() : RowDataHolder() {
            override val itemViewType: Int = ID_MEAL_OPTION
        }

        class RowProductGroupDataHolder(val productGroup: ProductGroup) : RowDataHolder() {
            override val itemViewType: Int = ID_PRODUCT_GROUP
        }

        class RowProductGroupModifierDataHolder(val product: Product, val modifierGroup: ModifierGroup) : RowDataHolder() {
            override val itemViewType: Int = ID_PRODUCT_GROUP_MODIFIER
        }
    }


    interface OnClickRowListener {
        fun onClickRowMealOptions()

        fun onClickRowProductGroup(productGroup: ProductGroup)

        fun onClickRowProductGroupModifier(product: Product, modifierGroup: ModifierGroup)
    }


}