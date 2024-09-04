<?php
$arr = glob("/Users/jim/Workdata/goland/src/jspp/k8sconfig-dev/*.yaml");
foreach($arr as $key=>$val) {
    $result = yaml_parse_file($val);
    var_export($result);
}
