import { createRouter, createWebHistory } from "vue-router";
import VerifyDb from "../views/VerifyDb.vue";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: "/",
      name: "VerifyDb",
      component: VerifyDb,
    },
  ],
});

export default router;
