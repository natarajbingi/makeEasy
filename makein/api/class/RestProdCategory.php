<?php
/**
 * Created by PhpStorm.
 * User: Admin
 * Date: 10/17/2019
 * Time: 11:27 PM
 */

include('Commons.php');

class RestProdCategory
{
    private $host = 'localhost';
    private $user = 'root';
    private $password = "";
    private $database = "make_in";
    private $prodCatTable = 'product_category';
    private $dbConnect = false;

    public function __construct()
    {
        if (!$this->dbConnect) {
            $conn = new mysqli($this->host, $this->user, $this->password, $this->database);
            if ($conn->connect_error) {
                die("Error failed to connect to MySQL: " . $conn->connect_error);
            } else {
                $this->dbConnect = $conn;
            }
        }
    }

    function insertProduct($productData, $files)
    {
        $commons = new Commons();

        $name = $productData["name"];
        $description = $productData["description"];
        $created_by = $productData["created_by"];
        $img_url = $commons->storeImg($productData["image"]);
//        $img_url = '/uploads/' . "makeIn" . $files['image']['name'];// . '.jpg';
//        $target = '../../uploads/';//. "makeIn" . $finalDate . '.jpg';
//        $target = $target . basename($files['image']['name']);
//        if (move_uploaded_file($files['image']['tmp_name'], $target)) {

        $empQuery = "INSERT INTO `product_category`( `name`, `description`, `visible`, `created_by`, `status`, `created_datetime`,"
            . "  `updated_datetime`, `img_url`) VALUES ('" . $name . "','$description','1','$created_by','1',NOW(),'','$img_url')";
        if (mysqli_query($this->dbConnect, $empQuery)) {
            $commons->json_enc(array());
        } else {
            $commons->json_enc("saving failed");
        }
//        } else {
//            $commons->json_enc("file move failed");
//        }


    }


}