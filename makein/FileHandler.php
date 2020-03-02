<?php
/**
 * Created by PhpStorm.
 * User: Nataraj Bingi
 * Date: 10/16/2019
 * Time: 11:42 PM
 */

class FileHandler
{

    private $con;

    public function __construct()
    {
        require_once dirname(__FILE__) . '/dbConnect.php';

        $db = new DbConnect();
        $this->con = $db->connect();
    }

    public function saveFile($file, $extension, $desc)
    {
        $name = round(microtime(true) * 1000) . '.' . $extension;
        $filedest = dirname(__FILE__) . UPLOAD_PATH . $name;
        move_uploaded_file($file, $filedest);

        $url = $server_ip = gethostbyname(gethostname());

        $stmt11 = $this->con->prepare("INSERT INTO images (description, image) VALUES (?, ?)");
        $stmt11->bind_param("ss", $desc, $name);
        if ($stmt11->execute())
            return true;
        return false;
    }

    public function getAllFiles()
    {
        $stmt = $this->con->prepare("SELECT id, description, url FROM images ORDER BY id DESC");
        $stmt->execute();
        $stmt->bind_result($id, $desc, $url);

        $images = array();

        while ($stmt->fetch()) {

            $temp = array();
            $absurl = 'http://' . gethostbyname(gethostname()) . '/ImageUploadApi' . UPLOAD_PATH . $url;
            $temp['id'] = $id;
            $temp['desc'] = $desc;
            $temp['url'] = $absurl;
            array_push($images, $temp);
        }

        return $images;
    }

    public function saveProd($file, $extension, $name, $description, $created_by)
    {
        $img_url = "";
        if ($file != null && $file != "") {
            $img_url = "prod" . round(microtime(true) * 1000) . '.' . $extension;
            $filedest = dirname(__FILE__) . UPLOAD_PATH . $img_url;
            move_uploaded_file($file, $filedest);
            $url = $server_ip = gethostbyname(gethostname());
        }
        $empQuery = "INSERT INTO `product_category`( `name`, `description`, `visible`, `created_by`, `status`, `created_datetime`,"
            . "  `updated_datetime`, `img_url`) VALUES ('" . $name . "','$description','1','$created_by','1',NOW(),'','$img_url')";

        if (mysqli_query($this->con, $empQuery))
            return true;
        return false;
        /*$stmt = $this->con->prepare("INSERT INTO images (description, url) VALUES (?, ?)");
        $stmt->bind_param("ss", $desc, $name);
        if ($stmt->execute())
            return true;
        return false;*/
    }

    public function updateProd($file, $extension, $name, $description, $id, $del, $created_by)
    {
        if ($del) {// deleting product
            $empQuery = "UPDATE `product_category` SET  `status`='0', `created_by`='$created_by', `updated_datetime`=NOW()  WHERE `id`='$id'";
        } else if ($file != "") {// updating  product with image
            $img_url = "prodUpdated" . round(microtime(true) * 1000) . '.' . $extension;
            $filedest = dirname(__FILE__) . UPLOAD_PATH . $img_url;
            move_uploaded_file($file, $filedest);
            $url = $server_ip = gethostbyname(gethostname());
            $empQuery = "UPDATE `product_category` SET `name`='$name',`description`='$description',`created_by`='$created_by', `updated_datetime`=NOW(),`img_url`='$img_url' WHERE `id`='$id'";
        } else {// updating  product withOut image
            $empQuery = "UPDATE `product_category` SET `name`='$name',`description`='$description',`created_by`='$created_by', `updated_datetime`=NOW()  WHERE `id`='$id'";
        }
        if (mysqli_query($this->con, $empQuery))
            return true;
        return false;

    }

    public function saveSubProd($file, $extension, $name, $description, $prod_id, $pur_cost, $sell_cost, $created_by)
    {
        $img_url = "";
        if ($file != "") {
            $img_url = "subprod" . round(microtime(true) * 1000) . '.' . $extension;
            $filedest = dirname(__FILE__) . UPLOAD_PATH . $img_url;
            move_uploaded_file($file, $filedest);
            $url = $server_ip = gethostbyname(gethostname());
        }
        $img_urlNew = $this->getExistImgs(0, $prod_id, $name, $description);
        if ($img_urlNew == "") {
            $img_urls = $img_url;
            $empQuery = "INSERT INTO `product_subcategory`( `name`, `description`, `prod_id`, `img_urls`, `pur_cost`, `sell_cost`, `createdby`, `status`, `visible`, `created_datetime`, `updated_datetime`)" .
                " VALUES ('$name','$description','$prod_id','$img_urls','$pur_cost','$sell_cost','$created_by','1','1',NOW(),'')";

        } else {
            $me = explode("_", $img_urlNew);
            $img_urls = $me[0] . "," . $img_url;
            $_id = $me[1];
            $empQuery = "UPDATE `product_subcategory` SET  `img_urls`='$img_urls'  WHERE `id`='$_id'";
        }
        if (mysqli_query($this->con, $empQuery))
            return true;
        return false;
    }

    public function updateSubProd($file, $extension, $name, $description, $pur_cost, $sell_cost, $id, $del, $created_by)
    {
        if ($del) {// deleting sub product
            $empQuery = "UPDATE `product_subcategory` SET  `status`='0', `created_by`='$created_by', `updated_datetime`=NOW()  WHERE `id`='$id'";
        } else if ($file != "") { // updating sub product with image
            $img_url = "subprodUpdated" . round(microtime(true) * 1000) . '.' . $extension;
            $filedest = dirname(__FILE__) . UPLOAD_PATH . $img_url;
            move_uploaded_file($file, $filedest);
            $url = $server_ip = gethostbyname(gethostname());

            $img_urlNew = $this->getExistImgs($id, 0, $name, $description);
            if ($img_urlNew == "") {
                $img_urls = $img_url;
            } else {
                $me = explode(",", $img_urlNew);
                $img_urls = $me[0] . "_" . $img_url;
                // $_id = $me[1];
                //  $empQuery = "UPDATE `product_subcategory` SET  `img_urls`='$img_urls'  WHERE `id`='$_id'";
            }
            $empQuery = "UPDATE `product_subcategory` SET  `name`='$name',`description`='$description',`img_urls`='$img_urls',`pur_cost`='$pur_cost',`sell_cost`='$sell_cost', `createdby`='$created_by', `updated_datetime`=NOW() WHERE `id`='$id'";
        } else {// updating  sub product withOut image
            $empQuery = "UPDATE `product_subcategory` SET  `name`='$name',`description`='$description', `pur_cost`='$pur_cost',`sell_cost`='$sell_cost', `createdby`='$created_by', `updated_datetime`=NOW() WHERE `id`='$id'";
        }

        if (mysqli_query($this->con, $empQuery))
            return true;
        return false;
    }

    public function saveUserRegister($file, $extension, $first_name, $last_name, $gender, $dob, $email_id, $passwd, $address_one
        , $address_two, $Landmark, $pincode, $mobile_no, $createdby)
    {
        if ($this->getExistUser($email_id, $mobile_no)) {
            return 101;
        }
        $profile_img = "";
        if ($file != "") {
            $profile_img = "profile" . round(microtime(true) * 1000) . '.' . $extension;
            $filedest = dirname(__FILE__) . UPLOAD_PATH . $profile_img;
            move_uploaded_file($file, $filedest);
            $url = $server_ip = gethostbyname(gethostname());
        }
        $empQuery = "INSERT INTO `users`(  `first_name`, `last_name`, `gender`,`dob`, `email_id`, `passwd`, `address_one`, `address_two`, `Landmark`, `pincode`, `mobile_no`, `createdby`, `status`, `created_datetime`, `updated_datetime`, `profile_img`)" .
            "VALUES ( '$first_name','$last_name','$gender','$dob','$email_id','$passwd','$address_one','$address_two', '$Landmark','$pincode','$mobile_no','$createdby','1',NOW(),'','$profile_img')";


        if (mysqli_query($this->con, $empQuery))
            return 200;
        return 102;
    }

    public function saveUserProdReq($user_id, $prod_id, $prod_subid, $quantity, $sell_cost, $delivery_address, $userContactNo
        , $userQuery, $userCompanyName, $userCompanyAddress, $userCompnyEmailAddress, $comment
    )
    {
        $date = new DateTime("NOW");
        $invoice_no = $date->format("dmY") . "INV" . $date->format("His");

        $empQuery = "INSERT INTO `user_prod_reqs`( `invoice_no`, `user_id`, `prod_id`, `prod_subid`, `quantity`, `sell_cost`, `delivery_address`," .
            " `status`, `deli_status`, `userContactNo`, `userQuery`, `userCompanyName`, `userCompanyAddress`, `userCompnyEmailAddress`," .
            " `comment`, `created_datetime`) VALUES ('$invoice_no','$user_id' ,'$prod_id','$prod_subid' ,'$quantity','$sell_cost', " .
            "'$delivery_address','1', 'PENDING' ,'$userContactNo','$userQuery','$userCompanyName','$userCompanyAddress' " .
            ",'$userCompnyEmailAddress','$comment',NOW())";

        if (mysqli_query($this->con, $empQuery)) {
            $token = $this->getExistToken('', 'admin');
            $token1 = $this->getExistToken('$user_id', 'user');
            $this->sendPushNotification('admin', 'New Request-'.$userCompanyName, $userQuery, $token);
            $this->sendPushNotification('user', 'Processing Request', 'Your request reached US, Will get back to soon thank you.', $token1);
            return $invoice_no;
        }
        return "";
    }

    public function updateUserProdReq($invoice_no, $id, $quantity, $sell_cost, $delivery_address, $deli_status, $user_id, $comment)
    {
        $quantitySet = "";
        if ($quantity != "") {
            $quantitySet = "`quantity`='$quantity',";
        }
        $sell_costSet = "";
        if ($sell_cost != "") {
            $sell_costSet = "`sell_cost`='$sell_cost',";
        }
        $delivery_addressSet = "";
        if ($delivery_address != "") {
            $delivery_addressSet = "`delivery_address`='$delivery_address',";
        }
        $commentSet = "";
        if ($comment != "") {
            $commentSet = "`comment`='$comment',";
        }
        $deli_statusSet = "";
        if ($deli_status != "") {
            $deli_statusSet = "`deli_status`='$deli_status',";
        }

        $empQuery = "UPDATE `user_prod_reqs` SET  $quantitySet $sell_costSet $delivery_addressSet $commentSet $deli_statusSet " .
            "`updated_datetime`=now() WHERE `id`='$id' or `invoice_no`='$invoice_no' && `user_id`=$user_id";


        if (mysqli_query($this->con, $empQuery))
            return $invoice_no;
        return "";
    }


    public function updateUserRegister($first_name, $last_name, $gender, $dob, $address_one,
                                       $address_two, $Landmark, $pincode, $mobile_no, $id, $del, $createdby)
    {
        if ($del) {// deleting User
            $empQuery = "UPDATE `users` SET  `status`='0', `createdby`='$createdby', `updated_datetime`=NOW()  WHERE  `id`='$id'";
        } else {
            $empQuery = "UPDATE `users` SET `first_name`='$first_name',`last_name`='$last_name',`gender`='$gender',`dob`='$dob',  `address_one`='$address_one', `address_two`='$address_two',`Landmark`='$Landmark' ,`pincode`='$pincode',`mobile_no`='$mobile_no',`createdby`='$createdby', `updated_datetime`=NOW()  WHERE `id`='$id'";
        }

        if (mysqli_query($this->con, $empQuery))
            return true;
        return false;
    }

    public function updateUserProfPic($file, $extension, $id, $createdby)
    {
        $profile_img = "";
        if ($file != "") {
            $profile_img = "profileUpdated" . round(microtime(true) * 1000) . '.' . $extension;
            $filedest = dirname(__FILE__) . UPLOAD_PATH . $profile_img;
            move_uploaded_file($file, $filedest);
            $url = $server_ip = gethostbyname(gethostname());
            $empQuery = "UPDATE `users` SET  `createdby`='$createdby', `updated_datetime`=NOW(),`profile_img`='$profile_img' WHERE `id`='$id'";
        }
       // $absurl = 'http://' . gethostbyname(gethostname()) . '/makeasy' . UPLOAD_PATH . $profile_img; 
       $absurl = 'http://snsproduction.com/makeasy' . UPLOAD_PATH . $profile_img;
        if (mysqli_query($this->con, $empQuery))
            return $absurl;
        return "";
    }

    public function updateUserPassword($email_id, $pwd, $id, $createdby)
    {

        $empQuery = "UPDATE `users` SET  `createdby`='$createdby', `updated_datetime`=NOW(), `passwd`='$pwd' WHERE `id`='$id' and `email_id`='$email_id' ";

        if (mysqli_query($this->con, $empQuery))
            return true;
        return false;
    }

    public function getExistImgs($id, $prod_id, $name, $description)
    {
        if ($id > 0) {
            $qry = "SELECT id,img_urls FROM product_subcategory where id = '$id'";
        } else {
            $qry = "SELECT id,img_urls FROM product_subcategory where name = '$name' and description = '$description' and prod_id = '$prod_id'";
        }
        $stmt = $this->con->prepare($qry);
        $stmt->execute();
        $stmt->bind_result($id, $img_urls);
        $res = "";
        while ($stmt->fetch()) {
            $res .= $img_urls . "_" . $id;
        }
        return $res;
    }

    public function getExistUser($email_id, $mobile_no)
    {
        $stmt = $this->con->prepare("SELECT id,first_name FROM users where email_id = '$email_id' and mobile_no = '$mobile_no' ");
        $stmt->execute();
        $stmt->bind_result($id, $img_urls);
        $res = 0;

        while ($stmt->fetch()) {
            $res++;
        }
        return $res > 0;
    }

    public function getExistToken($userID, $type)
    {
        $empQuery = "";

        if ($type == 'admin') {
            $empQuery = "SELECT `registrationID` FROM `users` WHERE `createdby`='ADMIN'";
        } else {
            $empQuery = "SELECT `registrationID` FROM `users` WHERE `id`='$userID'";
        }
        $stmt = $this->con->prepare($empQuery);
        $stmt->execute();
        $stmt->bind_result($registrationID);
        $res = "";

        while ($stmt->fetch()) {
            $res = $registrationID;
        }
        return $res;
    }

    public function getAllProds($withSub, $created_by)
    {
        $created_byMe = "";
        if ($created_by != "") {
            $created_byMe = " and `created_by`='$created_by'";
        }
        $stmt = $this->con->prepare("SELECT id, name, description, img_url, created_datetime FROM product_category WHERE `status`=1 and `visible`=1 " . $created_byMe . " ORDER BY id DESC");
        $stmt->execute();
        $stmt->bind_result($id, $name, $description, $img_url, $created_datetime);
        $images = array();
        while ($stmt->fetch()) {

            $temp = array();
            $absurl = 'http://snsproduction.com/makeasy' . UPLOAD_PATH . $img_url;
            $temp['id'] = $id;
            $temp['name'] = $name;
            $temp['description'] = $description;
            $temp['img_url'] = $absurl;
            $temp['created_datetime'] = $created_datetime;
            if ($withSub) {
                $mysqli = new mysqli(DB_HOST, DB_USER, DB_PASS, DB_NAME);
                $qry = "SELECT `id`, `name`, `description`, `prod_id`, `img_urls`, `pur_cost`, `sell_cost`, `createdby`,  `created_datetime`, `updated_datetime` FROM `product_subcategory` WHERE `status`=1 and `visible`=1 and prod_id='$id' ORDER BY id DESC";
                $posts = $mysqli->query($qry) or die('Error: ' . $mysqli->error);

                if ($posts->num_rows > 0) {
                    $suProds = array();
                    while ($row = $posts->fetch_assoc()) {
                        $temp1 = array();
                        $temp1['id'] = $row['id'];
                        $temp1['name'] = $row['name'];
                        $temp1['description'] = $row['description'];
                        $temp1['prod_id'] = $row['prod_id'];
                        $temp1['img_urls'] = $this->splitImgs($row['img_urls']);
                        $temp1['pur_cost'] = $row['pur_cost'];
                        $temp1['sell_cost'] = $row['sell_cost'];
                        $temp1['createdby'] = $row['createdby'];
                        $temp1['created_datetime'] = $row['created_datetime'];
                        $temp1['updated_datetime'] = $row['updated_datetime'];
                        array_push($suProds, $temp1);
                    }
                    $temp['subProds'] = $suProds;
                }
                $mysqli->close();
                /*else {

                    echo 'No records found.';

                }*/
            }
            array_push($images, $temp);
        }

        $this->con->close();
        $stmt->close();
        return $images;
    }

    public function getAllUsers($username, $pwd)
    {
        if ($username == "")
            $qry = "SELECT id, first_name, last_name, gender, email_id, passwd, dob,address_one, address_two, Landmark, pincode, mobile_no, createdby,  created_datetime, updated_datetime, profile_img, registrationID, appVersion, last_login  FROM users WHERE status=1 AND createdby != 'ADMIN' ORDER BY id DESC";
        else if ($username == "0")
            $qry = "SELECT id, first_name, last_name, gender, email_id, passwd, dob,address_one, address_two, Landmark, pincode, mobile_no, createdby,  created_datetime, updated_datetime, profile_img, registrationID, appVersion, last_login  FROM users WHERE status=1 AND id = $pwd  ORDER BY id DESC";
        else
            $qry = "SELECT id, first_name, last_name, gender, email_id, passwd,dob, address_one, address_two, Landmark, pincode, mobile_no, createdby,  created_datetime, updated_datetime, profile_img, registrationID, appVersion, last_login  FROM users WHERE status=1 and email_id = '$username' and passwd = '$pwd'  ORDER BY id DESC";

//        echo $qry;

        $stmt = $this->con->prepare($qry);
        $stmt->execute();
        $stmt->bind_result($id, $first_name, $last_name, $gender, $email_id, $passwd, $dob, $address_one, $address_two, $Landmark, $pincode, $mobile_no, $createdby, $created_datetime, $updated_datetime, $profile_img, $registrationID, $appVersion, $last_login);
        $profile = array();
        while ($stmt->fetch()) {
            $temp = array();
            $absurl = 'http://snsproduction.com/makeasy' . UPLOAD_PATH . $profile_img;
            $temp['id'] = $id;
            $temp['first_name'] = $first_name;
            $temp['last_name'] = $last_name;
            $temp['profile_img'] = $absurl;
            $temp['gender'] = $gender;
            $temp['email_id'] = $email_id;
            $temp['gender'] = $gender;
            $temp['dob'] = $dob;
            $temp['address_one'] = $address_one;
            $temp['address_two'] = $address_two;
            $temp['Landmark'] = $Landmark;
            $temp['pincode'] = $pincode;
            $temp['mobile_no'] = $mobile_no;
            $temp['createdby'] = $createdby;
            $temp['mobile_no'] = $mobile_no;
            $temp['created_datetime'] = $created_datetime;
            $temp['updated_datetime'] = $updated_datetime;
            $temp['registrationID'] = $registrationID;
            $temp['appVersion'] = $appVersion;
            $temp['last_login'] = $last_login;

            array_push($profile, $temp);
        }

        return $profile;
    }

    public function updateLoginUserDetails($username, $pwd, $registrationID, $deviceName, $imeiNumber, $appVersion)
    {
        $addStr = "";
        if ($registrationID != "") {
            $addStr = $addStr . " `registrationID`='$registrationID', ";
        }

        if ($deviceName != "") {
            $addStr = $addStr . " `deviceName`='$deviceName', ";
        }
        if ($imeiNumber != "") {
            $addStr = $addStr . " `imeiNumber`='$imeiNumber', ";
        }
        if ($appVersion != "") {
            $addStr = $addStr . " `appVersion`='$appVersion', ";
        }

        $empQuery = "UPDATE `users` SET  " . $addStr . " `last_login`=NOW() WHERE `email_id`='$username' AND `passwd`='$pwd' ";

        if (mysqli_query($this->con, $empQuery))
            return true;
        return false;
    }

    public function getAllSubProds($pr_id)
    {
        if ($pr_id > 0)
            $qry = "SELECT `id`, `name`, `description`, `prod_id`, `img_urls`, `pur_cost`, `sell_cost`, `createdby`,  `created_datetime`, `updated_datetime` FROM `product_subcategory` WHERE `status`=1 and `visible`=1 and prod_id='$pr_id' ORDER BY id DESC";
        else
            $qry = "SELECT `id`, `name`, `description`, `prod_id`, `img_urls`, `pur_cost`, `sell_cost`, `createdby`,  `created_datetime`, `updated_datetime` FROM `product_subcategory` WHERE `status`=1 and `visible`=1  ORDER BY id DESC";

        $stmt1 = $this->con->prepare($qry);
        $stmt1->execute();
        $stmt1->bind_result($id, $name, $description, $prod_id, $img_urls, $pur_cost, $sell_cost, $createdby, $created_datetime, $updated_datetime);

        $suProds = array();

        while ($stmt1->fetch()) {

            $temp = array();
            $temp['id'] = $id;
            $temp['name'] = $name;
            $temp['description'] = $description;
            $temp['prod_id'] = $prod_id;
            $temp['img_urls'] = $this->splitImgs($img_urls);
            $temp['pur_cost'] = $pur_cost;
            $temp['sell_cost'] = $sell_cost;
            $temp['createdby'] = $createdby;
            $temp['created_datetime'] = $created_datetime;
            $temp['updated_datetime'] = $updated_datetime;
            array_push($suProds, $temp);
        }

        return $suProds;
    }

    public function getAllProdReqs($usr_id, $deli_status)
    {
        //if deli_status is empty get all type
        //if user_id is 0 get all to admin else to particular user .
        $deli_req = "";
        if ($deli_status != "") {
            $deli_req = " and ureq.`deli_status`='$deli_status' ";
        }
        if ($usr_id == "0")
            $qry = "SELECT ureq.`id`, ureq.`invoice_no`,  

                    pcr.id AS prodid,  pcr.name AS prodName,  ureq.`quantity`,   pscr.id AS SpCid, pscr.name AS SpCName, pscr.img_urls, 
                    usr.id as userId,  CONCAT(usr.first_name,' ', usr.last_name)  AS usrName, usr.gender as gender,usr.mobile_no as mobile ,
                    
                    ureq.`sell_cost`,  ureq.`delivery_address`,  ureq.`status`,  ureq.`deli_status`,  ureq.`comment`,
                    ureq.userContactNo,  ureq.userQuery,  ureq.userCompanyName,  ureq.userCompanyAddress, ureq.userCompnyEmailAddress,
                    ureq.`created_datetime`,  ureq.`updated_datetime`
                    
                    FROM 
                    `user_prod_reqs` ureq ,  users usr ,   product_category pcr,   product_subcategory pscr 
                    
                    WHERE ureq.`user_id`=usr.id AND  ureq.`prod_id`=pcr.id AND  ureq.`prod_subid`=pscr.id and  ureq.`status`=1 
                   $deli_req ORDER BY ureq.id DESC  ";
        else
            $qry = "SELECT ureq.`id`, ureq.`invoice_no`,  

                    pcr.id AS prodid,  pcr.name AS prodName,  ureq.`quantity`,  pscr.id AS SpCid, pscr.name AS SpCName, pscr.img_urls,
                    usr.id as userId,  CONCAT(usr.first_name,' ', usr.last_name) AS usrName,  usr.gender as gender,usr.mobile_no as mobile ,
                    
                    ureq.`sell_cost`,  ureq.`delivery_address`,  ureq.`status`,  ureq.`deli_status`,  ureq.`comment`, 
                     ureq.userContactNo,  ureq.userQuery,  ureq.userCompanyName,  ureq.userCompanyAddress, ureq.userCompnyEmailAddress,
                    ureq.`created_datetime`,  ureq.`updated_datetime`
                    
                    FROM 
                    `user_prod_reqs` ureq ,  users usr ,  product_category pcr,  product_subcategory pscr 
                    
                    WHERE ureq.`user_id`='$usr_id' AND  ureq.`prod_id`=pcr.id AND  ureq.`prod_subid`=pscr.id and  ureq.`status`=1 
                  $deli_req  ORDER BY ureq.id DESC";

//       echo $qry;
        $stmt1 = $this->con->prepare($qry);
        $stmt1->execute();
        $stmt1->bind_result($id, $invoice_no, $prodid, $prodName, $quantity, $SpCid,
            $SpCName, $img_urls, $userId, $usrName, $gender, $mobile_no, $sell_cost, $delivery_address, $status, $deli_status,
            $comment,
            $userContactNo,   $userQuery,   $userCompanyName,   $userCompanyAddress,   $userCompnyEmailAddress,
            $created_datetime, $updated_datetime);
        $ProdReqs = array();
        while ($stmt1->fetch()) {

            $temp = array();
            $temp['id'] = $id;
            $temp['invoice_no'] = $invoice_no;
            $temp['prodid'] = $prodid;
            $temp['prodName'] = $prodName;
            $temp['quantity'] = $quantity;
            $temp['SubProdCatId'] = $SpCid;
            $temp['SubProdCatName'] = $SpCName;
            $temp['img_urls'] = $this->splitImgs($img_urls);;
            $temp['userId'] = $userId;
            $temp['usrName'] = $usrName;
            $temp['gender'] = $gender;
            $temp['mobile_no'] = $mobile_no;
            $temp['sell_cost'] = $sell_cost;
            $temp['delivery_address'] = $delivery_address;
            $temp['status'] = $status;
            $temp['deli_status'] = $deli_status;
            $temp['comment'] = $comment;
            $temp['userContactNo'] = $userContactNo;
            $temp['userQuery'] = $userQuery;
            $temp['userCompanyName'] = $userCompanyName;
            $temp['userCompanyAddress'] = $userCompanyAddress;
            $temp['userCompnyEmailAddress'] = $userCompnyEmailAddress;
            $temp['created_datetime'] = $created_datetime;
            $temp['updated_datetime'] = $updated_datetime;
            array_push($ProdReqs, $temp);
        }

        return $ProdReqs;
    }

    public function splitImgs($img_urls)
    {
        $imgUrl = explode(",", $img_urls);
        $imgArray = array();
        for ($x = 0; $x < count($imgUrl); $x++) {
            // $absurl = 'http://' . gethostbyname(gethostname()) . '/makein' . UPLOAD_PATH . $imgUrl[$x];
            $absurl = 'http://snsproduction.com/makeasy' . UPLOAD_PATH . $imgUrl[$x];
            array_push($imgArray, $absurl);
        }

        return $imgArray;
    }

    public function sendPushNotification($toUser_Admin, $title, $body, $token)
    {
        $API_ACCESS_KEY = "";
        if ($toUser_Admin == "user") {
            $API_ACCESS_KEY = API_ACCESS_KEY_USERS;
        } else {
            $API_ACCESS_KEY = API_ACCESS_KEY_ADMIN;
        }
        //   $registrationIds = ;
        #prep the bundle
        $msg = array
        (
            'body' => $body,//'Firebase Push Notification',
            'title' => $title//'Vishal Thakkar',

        );
        $fields = array
        (
            'to' => $token,//$_REQUEST['token'],
            'notification' => $msg
        );


        $headers = array
        (
            'Authorization: key=' . $API_ACCESS_KEY,
            'Content-Type: application/json'
        );
        #Send Reponse To FireBase Server
        $ch = curl_init();
        curl_setopt($ch, CURLOPT_URL, 'https://fcm.googleapis.com/fcm/send');
        curl_setopt($ch, CURLOPT_POST, true);
        curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
        curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);
        curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($fields));
        $result = curl_exec($ch);
        curl_close($ch);
        return $result;
    }

}