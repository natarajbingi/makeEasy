-- phpMyAdmin SQL Dump
-- version 4.9.0.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Dec 25, 2019 at 06:11 PM
-- Server version: 10.3.16-MariaDB
-- PHP Version: 7.3.7

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `make_in`
--

-- --------------------------------------------------------

--
-- Table structure for table `emp`
--

CREATE TABLE `emp` (
  `id` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  `skills` varchar(255) NOT NULL,
  `address` varchar(255) NOT NULL,
  `designation` varchar(255) NOT NULL,
  `age` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `emp`
--

INSERT INTO `emp` (`id`, `name`, `skills`, `address`, `designation`, `age`) VALUES
(0, 'Nataraj', 'ssgs dfd f', 'Banaglore', 'dgdfg rdg dfg', 22);

-- --------------------------------------------------------

--
-- Table structure for table `images`
--

CREATE TABLE `images` (
  `id` int(11) NOT NULL,
  `description` varchar(1000) NOT NULL,
  `image` varchar(500) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `images`
--

INSERT INTO `images` (`id`, `description`, `image`) VALUES
(1, 'test me', '1572370587569.jpg'),
(2, 'test me', '1572370822378.jpg'),
(3, 'test me', '1572371630224.jpg'),
(4, 'My Image', '1572371825978.jpg'),
(5, 'My Image', '1572371975233.jpg');

-- --------------------------------------------------------

--
-- Table structure for table `product_category`
--

CREATE TABLE `product_category` (
  `id` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  `description` varchar(255) NOT NULL,
  `visible` int(1) NOT NULL,
  `created_by` int(11) NOT NULL,
  `status` int(1) NOT NULL,
  `created_datetime` varchar(100) NOT NULL,
  `updated_datetime` varchar(100) NOT NULL,
  `img_url` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `product_category`
--

INSERT INTO `product_category` (`id`, `name`, `description`, `visible`, `created_by`, `status`, `created_datetime`, `updated_datetime`, `img_url`) VALUES
(1, 'test', 'best', 1, 1, 1, '2019-10-19 14:32:20', '', 'prod1571475740027.jpg'),
(2, 'test', 'best', 1, 1, 1, '2019-10-27 16:01:33', '', 'prod1572172293148.jpg'),
(3, 'fesf', 'cb.    ', 1, 1, 1, '2019-10-29 23:32:13', '', 'prod1572372133217.jpg'),
(5, 'tedv', 'dcv. ', 1, 1, 1, '2019-10-29 23:56:44', '', 'prod1572373604353.jpg'),
(6, 'Ong bak', 'test generator', 1, 1, 1, '2019-11-03 09:45:34', '', 'prod1572754534525.jpg'),
(7, 'treatment', 'testing description of what you think', 1, 1, 1, '2019-11-03 09:53:18', '', 'prod1572754998827.jpg');

-- --------------------------------------------------------

--
-- Table structure for table `product_subcategory`
--

CREATE TABLE `product_subcategory` (
  `id` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  `description` varchar(255) NOT NULL,
  `prod_id` int(11) NOT NULL,
  `img_urls` varchar(255) NOT NULL,
  `pur_cost` varchar(100) NOT NULL,
  `sell_cost` varchar(100) NOT NULL,
  `createdby` int(11) NOT NULL,
  `status` int(1) NOT NULL,
  `visible` int(1) NOT NULL,
  `created_datetime` varchar(100) NOT NULL,
  `updated_datetime` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `product_subcategory`
--

INSERT INTO `product_subcategory` (`id`, `name`, `description`, `prod_id`, `img_urls`, `pur_cost`, `sell_cost`, `createdby`, `status`, `visible`, `created_datetime`, `updated_datetime`) VALUES
(1, 'Sub one', 'Sub one desc', 1, 'Sub_one,Sub_one', '20', '30', 1, 1, 1, '2019-10-30 22:15:46', ''),
(5, 'bbb', 'hhh', 2, 'subprod1572456597939.jpg,subprod1572456598166.jpg,subprod1572456598333.jpg,subprod1572456598499.jpg', '9', '25', 1, 1, 1, '2019-10-30 22:59:57', ''),
(6, 'testimonials', 'testing description', 5, 'subprod1572457006183.jpg,subprod1572457006460.jpg,subprod1572457006601.jpg', '10', '25', 1, 1, 1, '2019-10-30 23:06:46', ''),
(7, 'bbbb', 'ccccc', 1, 'subprod1572457106372.jpg,subprod1572457106548.jpg,subprod1572457106699.jpg,subprod1572457106850.jpg', '25', '35', 1, 1, 1, '2019-10-30 23:08:26', ''),
(8, 'onek din dhore', 'testb for med school', 6, 'subprod1572755112750.jpg,subprod1572755112913.jpg', '15', '25', 1, 1, 1, '2019-11-03 09:55:12', ''),
(11, 'yesterday', 'testb for the first time', 7, 'subprod1572756490403.jpg,subprod1572756490578.jpg,subprod1572756490726.jpg,subprod1572756490884.jpg,subprod1572756491037.jpg,subprod1572756491190.jpg,subprod1572756491364.jpg,subprod1572756491522.jpg,subprod1572756491686.jpg,subprod1572756491862.jpg,subpr', '65', '95', 1, 1, 1, '2019-11-03 10:18:10', '');

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `id` int(11) NOT NULL,
  `first_name` varchar(255) NOT NULL,
  `last_name` varchar(255) NOT NULL,
  `gender` varchar(100) NOT NULL,
  `dob` varchar(50) NOT NULL,
  `email_id` varchar(255) NOT NULL,
  `passwd` varchar(255) NOT NULL,
  `address_one` varchar(255) NOT NULL,
  `address_two` varchar(255) NOT NULL,
  `Landmark` varchar(255) NOT NULL,
  `pincode` varchar(100) NOT NULL,
  `mobile_no` varchar(100) NOT NULL,
  `createdby` varchar(100) NOT NULL,
  `status` int(1) NOT NULL,
  `created_datetime` varchar(100) NOT NULL,
  `updated_datetime` varchar(100) NOT NULL,
  `profile_img` varchar(255) NOT NULL,
  `registrationID` varchar(255) NOT NULL,
  `deviceName` varchar(100) NOT NULL,
  `imeiNumber` varchar(100) NOT NULL,
  `appVersion` varchar(15) NOT NULL,
  `last_login` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `first_name`, `last_name`, `gender`, `dob`, `email_id`, `passwd`, `address_one`, `address_two`, `Landmark`, `pincode`, `mobile_no`, `createdby`, `status`, `created_datetime`, `updated_datetime`, `profile_img`, `registrationID`, `deviceName`, `imeiNumber`, `appVersion`, `last_login`) VALUES
(1, 'admin', 'Admin panel', 'MALE', '22-10-2019', 'admin@gmail.com', '123456', 'admin address on1', 'admin address tw2', 'admin Landmark', '560085', '9988008989', 'ADMIN', 1, 'NOW()', '2019-11-06 03:17:21', 'profileUpdated1572986827431.jpg', '', '', '', '', ''),
(2, 'test', 'best', 'MALE', '12/25/2001', 'ale@gmail.com', 'demo123', 'teath', 'beat them', 'lean muscle', '236598', '9966558800', '', 1, 'NOW()', '', '', '', '', '', '', ''),
(3, 'test', 'best', 'Male', '2/24/1988', 'mail@gmail.com', 'demo123', 'Merett', 'address_two', 'twsrLandmark', '324343', '855900900', 'Self', 1, '2019-11-05 23:57:47', '', 'profile1572978467499.jpg', '', '', '', '', ''),
(4, 'test1', 'best1', 'Male', '2/24/1988', 'mai1l@gmail.com', 'demo123', 'Merett', 'address_two1', 'twsrLandmark', '324343', '855900907', 'Self', 1, '2019-11-05 23:58:21', '', '', '', '', '', '', ''),
(5, 'Wu du', 'sub sub', 'MALE', '83-96-8099', 'thfh@vb.mm', 'demo123', 'fhg ydf', 'fhg at in g', 'fhg df u', '652386', '2255886699', '', 1, '2019-11-06 00:00:41', '', '', '', '', '', '', ''),
(6, 'dhf', 'cg ych', 'MALE', '56-96-9866', 'gfg@fhmkk.mk', 'demo123', 'ghhh', 'buff', 'tedy', '325698', '2356896523', 'Self', 1, '2019-11-06 00:05:39', '', '', '', '', '', '', ''),
(7, 'dhf', 'cg ych', 'MALE', '56-96-9866', 'gfeg@fhmkk.mk', 'demo123', 'ghhh', 'buff', 'tedy', '325698', '2356896523', 'Self', 1, '2019-11-06 00:07:27', '', '', '', '', '', '', ''),
(8, 'dhf', 'cg ych', 'MALE', '56-96-9866', 'gffheg@fhmkk.mk', 'demo123', 'ghhh', 'buff', 'tedy', '325698', '2356896541', 'Self', 1, '2019-11-06 00:20:56', '', 'profile1572979856804.jpg', '', '', '', '', '');

-- --------------------------------------------------------

--
-- Table structure for table `user_prod_reqs`
--

CREATE TABLE `user_prod_reqs` (
  `id` int(11) NOT NULL,
  `invoice_no` varchar(100) NOT NULL,
  `user_id` int(11) NOT NULL,
  `prod_id` int(11) NOT NULL,
  `prod_subid` int(11) NOT NULL,
  `quantity` varchar(100) NOT NULL,
  `sell_cost` varchar(100) NOT NULL,
  `delivery_address` varchar(255) NOT NULL,
  `status` int(1) NOT NULL,
  `deli_status` varchar(50) NOT NULL,
  `userContactNo` varchar(100) NOT NULL,
  `userQuery` varchar(255) NOT NULL,
  `userCompanyName` varchar(255) NOT NULL,
  `userCompanyAddress` varchar(255) NOT NULL,
  `userCompnyEmailAddress` varchar(100) NOT NULL,
  `comment` varchar(255) NOT NULL,
  `created_datetime` varchar(100) NOT NULL,
  `updated_datetime` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `images`
--
ALTER TABLE `images`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `product_category`
--
ALTER TABLE `product_category`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `product_subcategory`
--
ALTER TABLE `product_subcategory`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `user_prod_reqs`
--
ALTER TABLE `user_prod_reqs`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `images`
--
ALTER TABLE `images`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `product_category`
--
ALTER TABLE `product_category`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT for table `product_subcategory`
--
ALTER TABLE `product_subcategory`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT for table `user_prod_reqs`
--
ALTER TABLE `user_prod_reqs`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
