<template>
    <div class="all-products">
      <h1>All Products</h1>
      <ul>
        <li v-for="product in products" :key="product.id">
          {{ product.name }} - ${{ product.price }}
        </li>
      </ul>
    </div>
  </template>
  
  <script setup>
  import { ref, onMounted } from 'vue';
  import axios from 'axios';
  
  const products = ref([]);
  
  function loadProducts() {
    axios.get('http://localhost:8080/WebShopAppREST/rest/products/')
      .then(response => {
        products.value = response.data;
      })
      .catch(error => console.log(error));
  }
  
  onMounted(() => {
    loadProducts();
  });
  </script>
  
  <style scoped>
  .all-products {
    padding: 20px;
  }
  
  ul {
    list-style-type: none;
    padding: 0;
  }
  
  li {
    margin: 10px 0;
  }
  </style>
  