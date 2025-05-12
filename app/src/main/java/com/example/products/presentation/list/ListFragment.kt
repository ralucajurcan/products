package com.example.products.presentation.list

import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.products.databinding.FragmentListBinding
import com.example.products.presentation.product.ProductViewModel
import com.example.products.presentation.shared.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListFragment : Fragment() {

    // view binding
    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!

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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
        viewModel.products.observe(viewLifecycleOwner) {
            productsAdapter.updateProducts(it)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
                binding.loadingView.visibility = if (isLoading) View.VISIBLE else View.GONE
                binding.productsListView.visibility = if (isLoading) View.GONE else View.VISIBLE
                binding.swipeRefreshLayout.isRefreshing = isLoading
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}