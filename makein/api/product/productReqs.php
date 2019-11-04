<?php
/**
 * Created by PhpStorm.
 * User: Admin
 * Date: 10/18/2019
 * Time: 12:08 AM
 */
$requestMethod = $_SERVER["REQUEST_METHOD"];
//print_r($_POST);

$jfo = null;
if ($_SERVER['REQUEST_METHOD'] == "POST") {
    if (!empty($_POST)) {
        foreach ($_POST as $key => $value) {
            $jfo =  json_decode($key);
        }
        print_r($jfo->image);
    } else {
        print_r("400 innt");
    }
} else {
    print_r("400 full ou");
}
//include('../class/RestProdCategory.php');
//$api = new RestProdCategory();
//switch ($requestMethod) {
//    case 'POST':
//        switch ($_POST["type"]) {
//            case 1://insert
//                $api->insertProduct($_POST,$_FILES);
//                break;
//            case 2://update
//                break;
//            case 3://getAll or single
//                break;
//            case 0://delete
//                break;
//            default:
//                header("HTTP/1.0 405 Method Not Allowed");
//                break;
//        }
//        break;
//    default:
//        header("HTTP/1.0 405 Method Not Allowed");
//        break;
//}