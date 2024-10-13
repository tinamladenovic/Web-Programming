<template>
  <div class="hello">
    <h1>{{ msg }}</h1>
    
  </div>
</template>

<script setup>
import axios from 'axios';
import { onMounted } from 'vue';

defineProps({
  msg: {
    type: String,
    required: true
  }
})

onMounted(() => {
	loadProducts();
})

function loadProducts() {
	axios.get('http://localhost:8080/WebShopAppREST/rest/products/')
		.then(response => {
			console.log(response.data)
		})
    .catch(error => console.log(error));
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
h3 {
  margin: 40px 0 0;
}
ul {
  list-style-type: none;
  padding: 0;
}
li {
  display: inline-block;
  margin: 0 10px;
}
a {
  color: #42b983;
}
</style>
