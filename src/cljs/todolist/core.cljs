(ns todolist.core
    (:require [reagent.core :as reagent :refer [atom]]
              [reagent.session :as session]
              [secretary.core :as secretary :include-macros true]
              [accountant.core :as accountant]))

;; -------------------------
;; Views

(def todolist (atom [{:id 10:name "pick up the groceries"}
                     {:id 9999 :name "wash the car"}
                     {:id 888 :name "do the laundry"}]))
(def newname (atom ""))

(defn home-page []
  [:div [:h2 "Welcome to todolist"]
   (map-indexed (fn [idx item]
                  [:div "MY TODO NAME IS: "
                   (:name item)
                   " and my index is " idx
                   " and my id is " (:id item)
                   ]
                  )
                @todolist)
   [:input {:on-change (fn [event]
                         (let [_val (.-value
                                     (.-currentTarget
                                      event))]
                           (reset! newname _val )))}]

   [:span {:id "test"
           :className "testClassName"
           :on-click (fn [event]
                       (swap! todolist (fn [_todolist]
                                         (conj _todolist {:id 2000
                                                          :name @newname}))))}
    "add item"]])

(defn about-page []
  [:div [:h2 "About todolist"]
   [:div [:a {:href "/"} "go to the home page"]]])

(defn current-page []
  [:div [(session/get :current-page)]])

;; -------------------------
;; Routes

(secretary/defroute "/" []
  (session/put! :current-page #'home-page))

(secretary/defroute "/about" []
  (session/put! :current-page #'about-page))

;; -------------------------
;; Initialize app

(defn mount-root []
  (reagent/render [current-page] (.getElementById js/document "app")))

(defn init! []
  (accountant/configure-navigation!
    {:nav-handler
     (fn [path]
       (secretary/dispatch! path))
     :path-exists?
     (fn [path]
       (secretary/locate-route path))})
  (accountant/dispatch-current!)
  (mount-root))
