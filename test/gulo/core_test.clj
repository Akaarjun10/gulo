(ns gulo.core-test
  "Unit test the gulo.core namespace."
  (:use gulo.core
        [gulo.util :only (latlon-valid?)]
        [dwca.core]
        [cascalog.api]
        [cascalog.more-taps :as taps :only (hfs-delimited)]
        [midje sweet cascalog]
        [clojure.java.io :as io]
        [clojure.contrib.java-utils :only (delete-file-recursively)])
  (:import [com.google.common.io Files]))

(def uuid-pattern #"[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}")
(def uuid-pattern-str "[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}")

(fact "Test taxon-table."
  (let [occ-path (->> (io/resource "dwc.tsv") .getPath)
        source (taps/hfs-delimited occ-path)
        temp-dir (->> (io/resource "test") .getPath)
        sink-path (str temp-dir "/tax")
        sink-part (str sink-path "/part-00000")]
    (taxon-table (hfs-textline occ-path) sink-path)
    (slurp sink-part :encoding "utf-8") =>
    #"[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}	Puma concolor"))

(fact "Test location-table."
  (let [occ-path (->> (io/resource "dwc.tsv") .getPath)
        source (hfs-textline occ-path)
        temp-dir (->> (io/resource "test") .getPath)
        sink-path (str temp-dir "/loc")
        sink-part (str sink-path "/part-00000")]    
    (location-table (hfs-textline occ-path) sink-path)
    (slurp sink-part :encoding "utf-8") =>
    (just #"[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}\t40.8\t-122.3\tPOINT(-122.3 40.8)\n")))

(fact "Test tax-loc-table"
  (let [occ-path (->> (io/resource "dwc.tsv") .getPath)
        source (hfs-textline occ-path)
        temp-dir (->> (io/resource "test") .getPath)
        sink-path (str temp-dir "/tax-loc")
        tax-part (str temp-dir "/tax/part-00000")
        loc-part (str temp-dir "/loc/part-00000")
        sink-part (str sink-path "/part-00000")]
    (tax-loc-table occ-path tax-part loc-part sink-path)
    ;;(slurp sink-part :encoding "utf-8") => expected
    1 => 1
    ))

(fact "Test occ-tabcle"
  (let [occ-path (->> (io/resource "dwc.tsv") .getPath)
        source (hfs-textline occ-path)
        temp-dir (->> (io/resource "test") .getPath)
        sink-path (str temp-dir "/occ")
        tax-part (str temp-dir "/tax/part-00000")
        loc-part (str temp-dir "/loc/part-00000")
        tax-loc-part (str temp-dir "/tax-loc/part-00000")
        sink-part (str sink-path "/part-00000")]
    (occ-table occ-path tax-part loc-part tax-loc-part sink-path)
    1 => 1
    ))
