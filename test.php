<?php
foreach (glob("{test,main.c}", GLOB_BRACE) as $key => $val) {
    echo $val, "\n";
}
