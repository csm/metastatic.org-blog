(ns cryogen.push
  (:require [clojure.java.io :as io]
            [clojure.java.shell :as shell])
  (:import (java.io PushbackReader)
           (java.lang ProcessBuilder$Redirect)))

(defn -main [& args]
  (when-let [private-key (System/getenv "SSH_PRIVATE_KEY")]
    (.mkdir (io/file (str (System/getProperty "user.home") "/.ssh")))
    (spit (str (System/getProperty "user.home") "/.ssh/test")
          private-key))
  (let [config (read (PushbackReader. (io/reader "content/config.edn")))
        result (shell/sh "rsync"
                         "-av"
                         "."
                         (str (:ssh-user config) \@ (:ssh-host config) \: (:rsync-path config)))]
    (when (not (zero? (:exit result)))
      (prn "push failed"
           result))))