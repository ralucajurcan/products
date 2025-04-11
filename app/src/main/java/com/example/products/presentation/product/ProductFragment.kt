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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.Navigation
import androidx.navigation.navGraphViewModels
import com.bumptech.glide.Glide
import com.example.products.R
import com.example.products.common.model.Product
import com.example.products.databinding.FragmentProductBinding
import com.example.products.presentation.shared.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProductFragment : Fragment(R.layout.fragment_product) {
    private var _binding: FragmentProductBinding? = null
    private val binding get() = _binding!!

    // delegated properties
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
            if (productId != 0L) {
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
            } else {
                // clear everything for the "add new product" use case
                currentProduct = Product(
                    title = "",
                    description = "",
                    imageUrl = "",
                    creationTime = 0L,
                    updateTime = 0L
                )

                binding.titleView.setText("")
                binding.contentView.setText("")
                binding.imageUrlView.setText("")
                binding.imagePreview.setImageResource(R.drawable.placeholder)
            }
        }

        binding.checkButton.setOnClickListener {
            val time = System.currentTimeMillis()

            currentProduct = currentProduct.copy(
                title = binding.titleView.text.toString(),
                description = binding.contentView.text.toString(),
                updateTime = time,
                creationTime = if (currentProduct.id == 0L) time else currentProduct.creationTime,
                imageUrl = binding.imageUrlView.text.toString()
            )
            viewModel.saveProduct(currentProduct)

            // reset the selected productId - needed for adding a new product
            sharedViewModel.resetSelectedProductId()

            // usage of the nav graph VM
            navGraphViewModel.addViewedProduct(currentProduct.title)
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

        binding.cancelButton.setOnClickListener {
            sharedViewModel.resetSelectedProductId()
            Navigation.findNavController(it).popBackStack()
        }

        observeViewModel()

        viewModel.validationError.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun observeViewModel() {
        // launches a coroutine tied to the Fragment's lifecycle; when the fragment is destroyed, the coroutine is auto cancelled.
        lifecycleScope.launch {
            // the code only runs when the fragment's view is at least in the started state
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.saved.collect { isStateSaved ->
                    if (isStateSaved) {
                        binding.titleView.clearFocus()
                        binding.contentView.clearFocus()
                        hideKeyboard()

                        Toast.makeText(requireContext(), "Done!", Toast.LENGTH_SHORT).show()

                        Navigation.findNavController(binding.titleView).popBackStack()
                    }
                }
            }
        }
    }

    private fun hideKeyboard() {
        val inputMethodManager =
            requireActivity().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        val currentFocusView = requireActivity().currentFocus ?: View(requireContext())
        currentFocusView.clearFocus()
        inputMethodManager.hideSoftInputFromWindow(currentFocusView.windowToken, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}