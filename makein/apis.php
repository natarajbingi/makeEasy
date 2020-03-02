<?php
/**
 * Created by PhpStorm.
 * User: Nataraj Bingi.
 * Date: 10/16/2019
 * Time: 11:45 PM
 */


require_once dirname(__FILE__) . '/FileHandler.php';
require_once dirname(__FILE__) . '/Constants.php';

$response = array();

if (isset($_GET['apicall'])) {
    switch ($_GET['apicall']) {
        case 'upload':

            if (isset($_POST['desc']) && strlen($_POST['desc']) > 0 && $_FILES['image']['error'] === UPLOAD_ERR_OK) {
                $upload = new FileHandler();
                $file = $_FILES['image']['tmp_name'];
                $desc = $_POST['desc'];
                if ($upload->saveFile($file, getFileExtension($_FILES['image']['name']), $desc)) {
                    $response['error'] = false;
                    $response['message'] = 'File Uploaded Successfullly';
                }
            } else {
                $response['error'] = true;
                $response['message'] = 'Required parameters are not available';
            }

            break;

        case 'getallimages':

            $upload = new FileHandler();
            $response['error'] = false;
            $response['images'] = $upload->getAllFiles();

            break;
        /* category table handle*/
        /*1 addcategory*/
        case one:

            $upload = new FileHandler();
            $file = "";
            $exten = "";
            if (isset($_FILES['image']['name']) && $_FILES['image']['error'] === UPLOAD_ERR_OK) {
                $file = $_FILES['image']['tmp_name'];
                $exten = getFileExtension($_FILES['image']['name']);
            }
            $name = $_POST["name"];
            $description = $_POST["description"];
            $created_by = $_POST["created_by"];

            if ($upload->saveProd($file, $exten, $name, $description, $created_by)) {
                $response['error'] = false;
                $response['message'] = 'Category added successfully';
            } else {
                $response['error'] = true;
                $response['message'] = 'failed , please try again.';
            }
            break;
        /*2 updatecategory*/
        case two:

            $upload = new FileHandler();
            $file = "";
            $exten = "";
            if (isset($_FILES['image']['name']) && $_FILES['image']['error'] === UPLOAD_ERR_OK) {
                $file = $_FILES['image']['tmp_name'];
                $exten = getFileExtension($_FILES['image']['name']);
            }
            $id = $_POST["id"];
            $name = $_POST["name"];
            $description = $_POST["description"];
            $created_by = $_POST["created_by"];

            //                      $file, $exten, $name, $description, $id, $del, $created_by
            if ($upload->updateProd($file, $exten, $name, $description, $id, false, $created_by)) {
                $response['error'] = false;
                $response['message'] = 'Category Updated successfully';
            } else {
                $response['error'] = true;
                $response['message'] = 'failed , please try again.';
            }
            break;
        /*3 deletecategory*/
        case three:

            $upload = new FileHandler();
            $file = "";
            $exten = "";
            $name = "";
            $description = "";
            $id = $_POST["id"];
            $created_by = $_POST["created_by"];
            if ($upload->updateProd($file, $exten, $name, $description, $id, true, $created_by)) {
                $response['error'] = false;
                $response['message'] = 'Category Deleted successfully';
            } else {
                $response['error'] = true;
                $response['message'] = 'failed , please try again.';
            }
            break;

        /*Sub category table handle*/
        /*4 addsubcategory*/
        case four:

            $upload = new FileHandler();
            $file = "";
            $exten = "";
            if (isset($_FILES['image']['name']) && $_FILES['image']['error'] === UPLOAD_ERR_OK) {
                $file = $_FILES['image']['tmp_name'];
                $exten = getFileExtension($_FILES['image']['name']);
            }
            $name = $_POST["name"];
            $description = $_POST["description"];
            $prod_id = $_POST["prod_id"];
            $pur_cost = $_POST["pur_cost"];
            $sell_cost = $_POST["sell_cost"];
            $created_by = $_POST["created_by"];
            if ($upload->saveSubProd($file, $exten, $name, $description, $prod_id, $pur_cost, $sell_cost, $created_by)) {
                $response['error'] = false;
                $response['message'] = 'Sub Category added successfully';
            } else {
                $response['error'] = true;
                $response['message'] = 'failed , please try again.';
            }

            break;
        /*5 updatesubcategory*/
        case five:

            $upload = new FileHandler();
            $file = "";
            $exten = "";
            if (isset($_FILES['image']['name']) && $_FILES['image']['error'] === UPLOAD_ERR_OK) {
                $file = $_FILES['image']['tmp_name'];
                $exten = getFileExtension($_FILES['image']['name']);
            }
            $name = $_POST["name"];
            $description = $_POST["description"];
            $id = $_POST["id"];
            $pur_cost = $_POST["pur_cost"];
            $sell_cost = $_POST["sell_cost"];
            $created_by = $_POST["created_by"];
            if ($upload->updateSubProd($file, $exten, $name, $description, $pur_cost, $sell_cost, $id, false, $created_by)) {
                $response['error'] = false;
                $response['message'] = 'Sub Category updated successfully';
            } else {
                $response['error'] = true;
                $response['message'] = 'failed , please try again.';
            }

            break;
        /*6 deletesubcategory*/
        case six:

            $upload = new FileHandler();
            $file = "";
            $exten = "";
            $name = "";
            $description = "";
            $pur_cost = "";
            $sell_cost = "";
            $id = $_POST["id"];
            $created_by = $_POST["created_by"];
            if ($upload->updateSubProd($file, $exten, $name, $description, $pur_cost, $sell_cost, $id, true, $created_by)) {
                $response['error'] = false;
                $response['message'] = 'Sub Category updated successfully';
            } else {
                $response['error'] = true;
                $response['message'] = 'failed , please try again.';
            }

            break;

        /* User table handle*/
        /*7 userregister*/
        case seven:
            $upload = new FileHandler();
            $file = "";
            $exten = "000";
            if (isset($_FILES['image']['name']) && $_FILES['image']['error'] === UPLOAD_ERR_OK) {
                $file = $_FILES['image']['tmp_name'];
                $exten = getFileExtension($_FILES['image']['name']);
            }
            $first_name = $_POST["first_name"];
            $last_name = $_POST["last_name"];
            $gender = $_POST["gender"];
            $email_id = $_POST["email_id"];
            $passwd = $_POST["passwd"];
            $address_one = $_POST["address_one"];
            $address_two = $_POST["address_two"];
            $Landmark = $_POST["Landmark"];
            $dob = $_POST["dob"];
            $mobile_no = $_POST["mobile_no"];
            $pincode = $_POST["pincode"];
            $createdby = $_POST["createdby"];
            $rs = $upload->saveUserRegister($file, $exten, $first_name, $last_name, $gender, $dob, $email_id, $passwd, $address_one
                , $address_two, $Landmark, $pincode, $mobile_no, $createdby);
            if ($rs == 200) {
                $response['error'] = false;
                $response['message'] = 'User Registered successfully ';
                $response['data'] = null;
            } else {
                $response['error'] = true;
                $response['data'] = null;
                $rs == 101 ? $msg = 'User EmailID and Mobile no already exists, Please try again.' : $msg = 'failed , please try again.';
                $response['message'] = $msg;
            }
            break;
        /*8 userupdate*/
        case eight:
            $upload = new FileHandler();
            $first_name = $_POST["first_name"];
            $last_name = $_POST["last_name"];
            $gender = $_POST["gender"];
            $id = $_POST["id"];
            $address_one = $_POST["address_one"];
            $dob = $_POST["dob"];
            $address_two = $_POST["address_two"];
            $Landmark = $_POST["Landmark"];
            $mobile_no = $_POST["mobile_no"];
            $pincode = $_POST["pincode"];
            $createdby = $_POST["createdby"];
            if ($upload->updateUserRegister($first_name, $last_name, $gender, $dob, $address_one
                , $address_two, $Landmark, $pincode, $mobile_no, $id, false, $createdby)) {
                $response['error'] = false;
                $rsMe = array();
                $rsMe['id'] = $id;
                $response['data'] = setuserMe($rsMe);
                $response['message'] = 'User updated successfully';
            } else {
                $response['error'] = true;
                $response['message'] = 'failed , please try again.';
            }
            break;
        /*8.1 userprofpicupdate*/
        case userprofpicupdate:
            $upload = new FileHandler();
            $file = "";
            $exten = "";
            if (isset($_FILES['image']['name']) && $_FILES['image']['error'] === UPLOAD_ERR_OK) {
                $file = $_FILES['image']['tmp_name'];
                $exten = getFileExtension($_FILES['image']['name']);
            }
            $id = $_POST["id"];
            $createdby = $_POST["createdby"];
            $rs = $upload->updateUserProfPic($file, $exten, $id, $createdby);
            if ($rs != "") {
                $response['error'] = false;
                $response['message'] = 'User Profile Pic updated successfully -' . $rs;
            } else {
                $response['error'] = true;
                $response['message'] = 'failed , please try again.';
            }
            break;
        /*8.1 updateUserPassword*/
        case updateuserpas:
            $upload = new FileHandler();
            $id = $_POST["id"];
            $pwd = $_POST["pwd"];
            $email_id = $_POST["email_id"];
            $createdby = $_POST["createdby"];
            if ($upload->updateUserPassword($email_id, $pwd, $id, $createdby)) {
                $response['error'] = false;
                $response['message'] = 'User Password  updated successfully, Please Re-Login with new Credentials.';
            } else {
                $response['error'] = true;
                $response['message'] = 'failed , please try again.';
            }
            break;
        /*9 userdelete*/
        case nine:
            $upload = new FileHandler();
            $first_name = "";
            $last_name = "";
            $gender = "";
            $address_one = "";
            $address_two = "";
            $Landmark = "";
            $mobile_no = "";
            $pincode = "";
            $id = $_POST["id"];
            $createdby = $_POST["createdby"];
            // $email_id = $_POST["email_id"];
            // $passwd = $_POST["passwd"];
            if ($upload->updateUserRegister("", "", "", ""
                , "", "", "", "", $id, true, $createdby)) {
                $response['error'] = false;
                $response['message'] = 'User Deleted successfully';
            } else {
                $response['error'] = true;
                $response['message'] = 'failed , please try again.';
            }
            break;

        /* get all table details handle*/
        /*10 getallprods*/
        case ten:
            $created_by = $_POST["created_by"];
            $upload = new FileHandler();
            $res = $upload->getAllProds(false, $created_by);
            $response['error'] = !count($res) > 0;
            $response['data'] = $res;
            $response['message'] = count($res) > 0 ? " product details " : " No product added  yet.";


            break;
        /*11 getallprodsubs*/
        case oneone:
            $created_by = $_POST["created_by"];
            $upload = new FileHandler();
            $res = $upload->getAllProds(true, $created_by);
            $response['error'] = !count($res) > 0;
            $response['data'] = $res;
            $response['message'] = count($res) > 0 ? "product and sub product details " : " No product or sub product added  yet.";

            break;
        /*11 getallsubs*/
        case oneight:
            $upload = new FileHandler();
            $prod_id = $_POST["prod_id"];
            $res = $upload->getAllSubProds($prod_id);
            $response['error'] = !count($res) > 0;
            $response['data'] = $res;
            $response['message'] = count($res) > 0 ? "product and sub product details " : " No product or sub product added  yet.";

            break;
        /*12 getallusers*/
        case onetwo:
            $res = setuserMe($_POST);
            $response['error'] = !count($res) > 0;
            $response['data'] = $res;
            $response['message'] = count($res) > 0 ? "User found " : " No user registered yet.";
            break;

        /* User table handle*/
        /*13 userprodreq todo*/
        case onethree:
            $upload = new FileHandler();
            $user_id = $_POST["user_id"];
            $prod_id = $_POST["prod_id"];
            $prod_subid = $_POST["prod_subid"];
            $quantity = $_POST["quantity"];
            $sell_cost = $_POST["sell_cost"];
            $delivery_address = $_POST["delivery_address"];
            // $deli_status = $_POST["deli_status"];// PENDING,DELIVERED,CANCELLED
            $userContactNo = $_POST["userContactNo"];
            $userQuery = $_POST["userQuery"];
            $userCompanyName = $_POST["userCompanyName"];
            $userCompanyAddress = $_POST["userCompanyAddress"];
            $userCompnyEmailAddress = $_POST["userCompnyEmailAddress"];
            $comment = $_POST["comment"];
            $res = $upload->saveUserProdReq($user_id, $prod_id, $prod_subid, $quantity, $sell_cost, $delivery_address, $userContactNo
                , $userQuery, $userCompanyName, $userCompanyAddress, $userCompnyEmailAddress, $comment);
            if ($res != "") {
                $response['error'] = false;
                $response['invoice'] = $res;
                $response['message'] = 'Product requested successfully, Admin will get back to you soon or you will get updates on App, thank you for request.';
            } else {
                $response['error'] = true;
                $response['message'] = 'failed , please try again.';
            }
            break;
        /*14 userprodrequpdate todo*/
        case onefour:
            $upload = new FileHandler();
            $invoice_no = $_POST["invoice_no"];
            $id = $_POST["id"];
            $quantity = $_POST["quantity"];
            $sell_cost = $_POST["sell_cost"];
            $delivery_address = $_POST["delivery_address"];
            $deli_status = $_POST["deli_status"];
            $comment = $_POST["comment"];
            $user_id = $_POST["user_id"];
            if ($upload->updateUserProdReq($invoice_no, $id, $quantity, $sell_cost, $delivery_address, $deli_status, $user_id, $comment)) {
                $response['error'] = false;
                $response['message'] = 'User updated successfully';
            } else {
                $response['error'] = true;
                $response['message'] = 'failed , please try again.';
            }
            break;
        /*15 userprodreqdelete todo*/
        case onefive:
            $upload = new FileHandler();
            $file = "";
            $exten = "";
            $first_name = "";
            $last_name = "";
            $gender = "";
            $address_one = "";
            $address_two = "";
            $Landmark = "";
            $mobile_no = "";
            $pincode = "";
            $id = $_POST["id"];
            $createdby = $_POST["createdby"];
            // $email_id = $_POST["email_id"];
            // $passwd = $_POST["passwd"];

            break;


        /*15 getAllProdReqs todo*/
        case getAllProdReqs:

            $upload = new FileHandler();
            $user_id = $_POST["user_id"];
            $deli_status = $_POST["deli_status"];
            $res = $upload->getAllProdReqs($user_id, $deli_status);
            $response['error'] = !count($res) > 0;
            $response['data'] = $res;
            $response['message'] = count($res) > 0 ? "User requests details " : "No User requests added  yet.";

            break;
        /*15 userprodreqgetll todo*/
        case userprodreqgetll:
            $upload = new FileHandler();
            $id = $_POST["id"];
            $createdby = $_POST["createdby"];
            $user_id = $_POST["user_id"];
            // $email_id = $_POST["email_id"];
            // $passwd = $_POST["passwd"];

            break;

        /* Login handle*/
        /*16 login*/
        case onesix:
            $res = setuserMe($_POST);
            $response['error'] = !count($res) > 0;
            $response['data'] = $res;
            $response['message'] = count($res) > 0 ? "Logged in successfully" : " Login failed.";
            break;
        /* PushNotification handle*/
        /*19 pushnoti*/
        case onenine:
            $upload = new FileHandler();
            $toUser_Admin = $_POST["toUser_Admin"];
            $title = $_POST["title"];
            $body = $_POST["body"];
            $token = $_POST["token"];

            $res = $upload->sendPushNotification($toUser_Admin, $title, $body, $token);
            $response['error'] = ($res) != null;
            $response['data'] = json_decode($res, TRUE);
            $response['message'] = ($res) != null ? "Notified successfully" : "  failed to Notify.";
            break;
    }
}

function setuserMe($POST)
{
    $upload = new FileHandler();
    if (isset($POST['username']) && isset($POST['pwd'])) {
        $res = $upload->getAllUsers($POST["username"], $POST['pwd']); // check for login
        if (count($res) > 0) {
            $upload->updateLoginUserDetails($POST['username'], $POST['pwd'], $POST['registrationID'], $POST['deviceName'], $POST['imeiNumber'], $POST['appVersion']);
        }
    } else if (isset($POST['getAllUsers'])) {
        $res = $upload->getAllUsers("", ""); // get all users
    } else if (isset($POST['id'])) {
        $res = $upload->getAllUsers("0", $POST['id']); // get particular user
    } else {
        return "";
    }
    return $res;
}

echo json_encode($response);

function getFileExtension($file)
{
    $path_parts = pathinfo($file);
    return $path_parts['extension'];
}

function storeImg()
{


// receive image as POST Parameter
    $image = str_replace('data:image/png;base64,', '', $_POST['image']);
    $image = str_replace(' ', '+', $image);
// Decode the Base64 encoded Image
    $data = base64_decode($image);
// Create Image path with Image name and Extension
    $file = './uploads/' . "MyImage" . '.jpg';
// Save Image in the Image Directory
    $success = file_put_contents($file, $data);

}