<template>
  <div class="factory-profile" v-if="factory">
    <div class="factory-info">
      <img :src="getFactoryLogo(factory.logo)" alt="Logo" class="factory-logo" />
      <h2>{{ factory.name }}</h2>
      <form>
        <div class="row mb-3">
          <label for="openingTime" class="col-sm-3 col-form-label text-start">Opening Hours:</label>
          <div class="col-sm-9">
            <input type="text" id="openingTime" v-model="factory.openingTime" class="form-control" readonly>
            <span> - </span>
            <input type="text" v-model="factory.closingTime" class="form-control" readonly>
          </div>
        </div>
        <div class="row mb-3">
          <label for="status" class="col-sm-3 col-form-label text-start">Status:</label>
          <div class="col-sm-9">
            <input type="text" id="status" v-model="factory.status" class="form-control" readonly>
          </div>
        </div>
        <div class="row mb-3">
          <label for="location" class="col-sm-3 col-form-label text-start">Location:</label>
          <div class="col-sm-9">
            <input type="text" id="location" v-model="factory.location.address" class="form-control" readonly>
          </div>
        </div>
        <div id="factory-map" class="map-container"></div>
        <div class="row mb-3" v-if="factory.rating !== undefined">
          <label for="rating" class="col-sm-3 col-form-label text-start">Rating:</label>
          <div class="col-sm-9">
            <input type="text" id="rating" v-model="factory.rating" class="form-control" readonly>
          </div>
        </div>
      </form>
    </div>

    <div class="factory-chocolates">
      <h3>Chocolates offered by the factory:</h3>
      <table>
        <thead>
          <tr>
            <th>Name</th>
            <th>Price</th>
            <th>Type</th>
            <th>Kind</th>
            <th>Weight</th>
            <th>Description</th>
            <th>Status</th>
            <th>Quantity</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="chocolate in chocolates" :key="chocolate.id" class="chocolate-item">
            <td>{{ chocolate.name }}</td>
            <td>{{ chocolate.price }}</td>
            <td>{{ chocolate.type }}</td>
            <td>{{ chocolate.kind }}</td>
            <td>{{ chocolate.weight }}g</td>
            <td>{{ chocolate.description }}</td>
            <td>{{ chocolate.status }}</td>
            <td>{{ chocolate.quantity }}</td>
          </tr>
        </tbody>
      </table>
    </div>

    <div class="factory-comments">
      <h3>Comments:</h3>
      <ul v-if="factory.comments && factory.comments.length">
        <li v-for="comment in factory.comments" :key="comment.id" class="comment-item">
          <div class="comment-header">
            <strong>User:</strong> {{ comment.user }}
          </div>
          <div class="comment-text">
            <p>{{ comment.text }}</p>
          </div>
        </li>
      </ul>
      <p v-else>No comments available.</p>
    </div>
  </div>
</template>

<script>
import axios from 'axios';
import { Map, View } from 'ol';
import TileLayer from 'ol/layer/Tile';
import OSM from 'ol/source/OSM';
import Feature from 'ol/Feature';
import Point from 'ol/geom/Point';
import { fromLonLat } from 'ol/proj';
import VectorLayer from 'ol/layer/Vector';
import { Vector as VectorSource } from 'ol/source';
import { Icon, Style } from 'ol/style';

export default {
  props: {
    id: String,
  },
  data() {
    return {
      factory: null,
      chocolates: [],
    };
  },
  methods: {
    loadFactory() {
      axios.get(`http://localhost:8080/WebShopAppREST/rest/chocolateFactoryObjects/${this.id}`)
        .then(response => {
          this.factory = response.data;
          this.loadChocolates();
          this.$nextTick(this.initializeMap);
        })
        .catch(error => {
          console.error("Error loading factory:", error);
        });
    },
    loadChocolates() {
      if (this.factory) {
        axios.get(`http://localhost:8080/WebShopAppREST/rest/chocolates`)
          .then(response => {
            this.chocolates = response.data.filter(chocolate => chocolate.factoryId === this.factory.factoryId);
          })
          .catch(error => {
            console.error("Error loading chocolates:", error);
          });
      }
    },
    getFactoryLogo(logoFileName) {
      return `/images/${logoFileName}`;
    },
    initializeMap() {
      if (this.factory) {
        const coordinates = fromLonLat([this.factory.location.longitude, this.factory.location.latitude]);
        new Map({
          target: 'factory-map',
          layers: [
            new TileLayer({
              source: new OSM()
            }),
            new VectorLayer({
              source: new VectorSource({
                features: [
                  new Feature({
                    geometry: new Point(coordinates)
                  })
                ]
              }),
              style: new Style({
                image: new Icon({
                  src: '/images/map-marker.png',
                  imgSize: [32, 32],
                  anchor: [0.5, 1]
                })
              })
            })
          ],
          view: new View({
            center: coordinates,
            zoom: 15
          })
        });
      }
    },
  },
  mounted() {
    this.loadFactory();
  },
};
</script>

<style scoped>
.d-flex {
  min-height: 100vh;
}

.card {
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 30px;
  background-color: #f0f8ff;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

h2 {
  color: #343a40;
  text-align: center;
  font-size: 2rem;
  font-weight: normal;
}

.form-label {
  font-weight: normal;
}

input[type="text"],
select,
button {
  border: 1px solid #007bff;
  border-radius: 8px;
  font-size: 1.1rem;
  transition: border-color 0.3s ease, box-shadow 0.3s ease;
}

input[type="text"]:focus,
select:focus {
  border-color: #007bff;
  box-shadow: 0 0 5px rgba(0, 123, 255, 0.25);
}

button[type="submit"],
.btn-outline-secondary {
  border: 1px solid #007bff;
  background-color: transparent;
  color: #007bff;
  font-size: 1.1rem;
  transition: background-color 0.3s ease, color 0.3s ease;
}

button[type="submit"]:hover,
.btn-outline-secondary:hover {
  background-color: #007bff;
  color: white;
}

button[type="submit"] {
  margin-top: 20px;
}

.text-danger {
  margin-top: 15px;
}

.text-success {
  margin-top: 15px;
}

.factory-profile {
  display: flex;
  gap: 20px;
  padding: 20px;
  max-width: 100%;
  margin: 0 auto;
}

.factory-info,
.factory-chocolates,
.factory-comments {
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 30px;
  background-color: #f0f8ff;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  flex: 1;
}

.factory-info {
  max-width: 500px;
}

.map-container {
  width: 100%;
  height: 300px;
  margin-top: 20px;
  border-radius: 8px;
}

.factory-logo {
  max-width: 100%;
  height: auto;
  margin-bottom: 10px;
  border-radius: 8px;
}

table {
  width: 100%;
  border-collapse: collapse;
  margin-bottom: 20px;
}

th, td {
  border: 1px solid #ddd;
  padding: 8px;
  text-align: left;
}

th {
  background-color: #f5e7f5;
  font-weight: bold;
}
</style>
