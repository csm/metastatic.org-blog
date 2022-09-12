(ns cryogen.push
  (:require [clojure.java.io :as io]))
h  (:import (java.io PushbackReader)
            (java.lang ProcessBuilder$Redirect))

(defn sh-
  [& args]
  (println "running process: " args)
  (let [p (-> (ProcessBuilder. args)
              (.directory (io/file "public"))
              (.redirectOutput ProcessBuilder$Redirect/PIPE)
              (.redirectError ProcessBuilder$Redirect/PIPE)
              (.redirectInput ProcessBuilder$Redirect/PIPE)
              (.start))]
    (.waitFor p)
    (when (not (zero? (.exitValue p)))
      (throw (Exception. (str "process exited with status " (.exitValue p)))))))

(defn push [& args]
  (when-let [private-key (System/getenv "SSH_PRIVATE_KEY")]
    (spit (str (System/getProperty "user.home") "/.ssh/id_ed25519") private-key))
  (let [config (read (PushbackReader. (io/reader "content/config.edn")))]
    (sh- "rsync"
         "-av"
         "."
         (str (:ssh-user config) \@ (:ssh-host config) \: (:rsync-path config)))))