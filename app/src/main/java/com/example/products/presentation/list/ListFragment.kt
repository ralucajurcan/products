package com.example.products.presentation.list

import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.products.databinding.FragmentListBinding
import com.example.products.presentation.product.ProductViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListFragment : Fragment() {

    // view binding
    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!

    // gives you an instance of ProductViewModel scoped to this Fragment
    private val viewModel: ProductViewModel by viewModels()

    // should navigate to product fragment
    private val productsAdapter = ProductsAdapter(emptyList()) { product ->
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

        // fetch from network and store data into db
        viewModel.syncProductListFromServer()

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.products.observe(viewLifecycleOwner) {
            binding.productsListView.visibility = View.VISIBLE
            binding.loadingView.visibility = View.GONE
            productsAdapter.updateProducts(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}