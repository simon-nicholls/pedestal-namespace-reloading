(ns pedestal-namespace-reloading.core
  (:require [io.pedestal.http :as server]
            [io.pedestal.http.route :as route]
            [io.pedestal.interceptor :as interceptor]
            [ns-tracker.core :refer [ns-tracker]]))

(defn add-namespace-reloading
  "Adds a namespace reloading interceptor to the interceptor chain"
  [service-map namespaces]
  (let [modified-namespaces (ns-tracker namespaces)
        reload-interceptor (interceptor/before
                            (fn [context]
                              (doseq [ns-sym (modified-namespaces)]
                                (require ns-sym :reload))
                              context))]
    (update-in service-map [::server/interceptors]
               (partial mapcat (fn [interceptor]
                           (if (= (:name interceptor) ::route/router)
                             [reload-interceptor interceptor]
                             [interceptor]))))))
