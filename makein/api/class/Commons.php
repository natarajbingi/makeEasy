<?php
/**
 * Created by PhpStorm.
 * User: Admin
 * Date: 10/17/2019
 * Time: 11:47 PM
 */

class Commons
{

    private $host = 'localhost';
    private $user = 'root';
    private $password = "";
    private $database = "make_in";
    private $prodCatTable = 'product_category';
    private $dbConnect = false;

    public function __construct()
    {

    }

    function json_enc($str)
    {
        $response = array();
        header('Content-Type: application/json');
        $response["success"] = is_array($str);
        if (is_array($str)) {
            $response["message"] = "Request done successfully";
            $response["data"] = json_encode($str);
        } else {
            $response["message"] = $str;
//            $response["data"] = $str;
        }
        print_r(json_encode($response));
        return;
    }

    function storeImg($img)
    {
        $date = new DateTime("NOW");
        $finalDate = $date->format("dmYHis");//.u" );
        // receive image as POST Parameter
        $image = str_replace('data:image/png;base64,', '', $img);
//        $image = str_replace('data:image/png;base64,', '', $img["img_url"]);
        $image = str_replace(' ', '+', $image);
        // Decode the Base64 encoded Image
        $data = base64_decode($image);
        // Create Image path with Image name and Extension
        $file = '../../uploads/' . "makeIn" . $finalDate . '.jpg';
        // Save Image in the Image Directory
        $success = file_put_contents($file, $data);

        if ($success) {
            return $file;
        } else {
            return "";
        }
    }
}