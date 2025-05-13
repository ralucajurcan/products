package com.example.products.presentation.list

import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.products.databinding.FragmentListBinding
import com.example.products.presentation.product.ProductViewModel
import com.example.products.presentation.shared.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ListFragment : Fragment() {

    // view binding
    private var _binding: FragmentListBinding? = null

    // gives an instance of ProductViewModel scoped to this Fragment
    private val viewModel: ProductViewModel by viewModels()

    // gives an instance of SharedViewModel scoped to the activity
    private val sharedViewModel: SharedViewModel by activityViewModels()

    // should navigate to product fragment
    private val productsAdapter = ProductsAdapter(emptyList()) { product ->
        // first store the product id in the shared view model, which can be observed by any other fragment in the activity
        sharedViewModel.selectProduct(product.id)
        val action = ListFragmentDirections.actionGoToProduct(product.id)
        findNavController().navigate(action)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListBinding.inflate(inflater, container, false)
        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = _binding ?: return

        binding.productsListView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = productsAdapter
        }

        binding.addProduct.setOnClickListener {
            val action = ListFragmentDirections.actionGoToProduct(0L)
            findNavController().navigate(action)
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.syncProductsFromNetwork()
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    val binding = _binding ?: return@collect
                    binding.loadingView.visibility = if (state.isLoading) View.VISIBLE else View.GONE
                    binding.productsListView.visibility = if (state.isLoading) View.GONE else View.VISIBLE

                    productsAdapter.updateProducts(state.products)

                    if (state.validationError != null) {
                        Toast.makeText(requireContext(), state.validationError, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}