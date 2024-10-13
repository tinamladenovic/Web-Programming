import { createRouter, createWebHashHistory } from 'vue-router';
import AllChocolateFactories from '../views/AllChocolateFactories.vue'; 

const routes = [
  {
    path: '/',
    name: 'allfactoriesview', 
    component: AllChocolateFactories 
  },
  {
    path: '/factoryprofile/:id', 
    name: 'factoryprofile',
    component: () => import(/*  webpackChunkName: "factoryprofile" */ '../views/FactoryProfile.vue'),
    props: true
    
  },
  {
    path: '/chocolates', 
    name: 'allchocolates',
    component: () => import(/* webpackChunkName: "allchocolates" */ '../views/AllChocolates.vue'),
  },
  {
    path: '/edit-chocolate/:id',
    name: 'editchocolate',
    component: () => import(/* webpackChunkName: "editchocolate" */ '../views/EditChocolate.vue'),
    props: true
  },
  {
    path: '/addchocolate',
    name: 'addchocolate',
    component: () =>
      import(/* webpackChunkName: "addchocolate" */ '../views/AddChocolate.vue'),
  },
  {
    path: '/login',
    name: 'login',
    component: () =>
      import(/* webpackChunkName: "login" */ '../views/Login.vue'),
  },
  {
    path: '/register',
    name: 'register',
    component: () =>
      import(/* webpackChunkName: "register" */ '../views/Register.vue'),
  },
  {
    path: '/logout',
    name: 'logout',
    component: () =>
      import(/* webpackChunkName: "logout" */ '../views/Logout.vue'),
  },

  {
    path: '/profile',
    name: 'profile',
    component: () =>
      import(/* webpackChunkName: "profile" */ '../views/UserProfile.vue'),
  },

  {
    path: '/users',
    name: 'users',
    component: () =>
      import(/* webpackChunkName: "users" */ '../views/UserTable.vue'),
  },

  {
    path: '/addfactory',
    name: 'addfactory',
    component: () =>
      import(/* webpackChunkName: "addfactory" */ '../views/AddFactory.vue'),
  },

  {
    path: '/addmanager',
    name: 'addmanager',
    component: () =>
      import(/* webpackChunkName: "addmanager" */ '../views/AddManager.vue'),
  },

  {
    path: '/addworker',
    name: 'addworker',
    component: () =>
      import(/* webpackChunkName: "addworker" */ '../views/AddWorker.vue'),
  },

  {
    path: '/addlocation',
    name: 'addlocation',
    component: () =>
      import(/* webpackChunkName: "addlocation" */ '../views/AddLocation.vue'),
  },

  {
    path: '/managerview',
    name: 'managerview',
    component: () =>
      import(/* webpackChunkName: "managerview" */ '../views/ManagerView.vue'),
  },
  
  {
    path: '/workerview',
    name: 'workerview',
    component: () =>
      import(/* webpackChunkName: "workerview" */ '../views/WorkerView.vue'),
  },

  
];

const router = createRouter({
  history: createWebHashHistory(),
  routes
});

export default router;
