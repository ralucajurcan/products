package com.example.products.presentation.product

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.navigation.navGraphViewModels
import com.bumptech.glide.Glide
import com.example.products.R
import com.example.products.common.model.Product
import com.example.products.databinding.FragmentProductBinding
import com.example.products.presentation.shared.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductFragment : Fragment(R.layout.fragment_product) {
    private var _binding: FragmentProductBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProductViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val navGraphViewModel: ProductsNavGraphViewModel by navGraphViewModels(R.id.products_graph)

    private var currentProduct = Product("", "", 0L, 0L, 0L, "")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // as an example - usage of the activity VM
        sharedViewModel.selectedProductId.observe(viewLifecycleOwner) { productId ->
            viewModel.getProduct(productId).observe(viewLifecycleOwner) { product ->
                product?.let {
                    currentProduct = it
                    binding.titleView.setText(it.title)
                    binding.contentView.setText(it.description)
                    binding.imageUrlView.setText(it.imageUrl)
                    Glide.with(requireContext())
                        .load(it.imageUrl)
                        .placeholder(R.drawable.placeholder)
                        .into(binding.imagePreview)
                }
            }
        }

        binding.checkButton.setOnClickListener {
            if (binding.titleView.text.isNotBlank() || binding.contentView.text.isNotBlank()) {
                val time = System.currentTimeMillis()

                currentProduct = currentProduct.copy(
                    title = binding.titleView.text.toString(),
                    description = binding.contentView.text.toString(),
                    updateTime = time,
                    creationTime = if (currentProduct.id == 0L) time else currentProduct.creationTime,
                    imageUrl = binding.imageUrlView.text.toString()
                )
                viewModel.saveProduct(currentProduct)

                // usage of the nav graph VM
                navGraphViewModel.addViewedProduct(currentProduct.title)
            } else {
                Navigation.findNavController(it).popBackStack()
            }
        }

        binding.imageUrlView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Glide.with(requireContext())
                    .load(s.toString())
                    .placeholder(R.drawable.placeholder)
                    .into(binding.imagePreview)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.saved.observe(viewLifecycleOwner, Observer {
            if (it) {
                Toast.makeText(context, "Done!", Toast.LENGTH_SHORT).show()
                binding.titleView.clearFocus()
                binding.contentView.clearFocus()
                hideKeyboard()
                Navigation.findNavController(binding.titleView).popBackStack()
            } else {
                Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun hideKeyboard() {
        val inputMethodManager = requireActivity().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        val currentFocusView = requireActivity().currentFocus ?: View(requireContext())
        currentFocusView.clearFocus()
        inputMethodManager.hideSoftInputFromWindow(currentFocusView.windowToken, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}