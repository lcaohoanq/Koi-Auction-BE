-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Máy chủ: mysql_db_koi_development
-- Thời gian đã tạo: Th9 28, 2024 lúc 02:17 AM
-- Phiên bản máy phục vụ: 9.0.1
-- Phiên bản PHP: 8.2.23

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Cơ sở dữ liệu: `koi_company_dev`
--

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `users`
--

CREATE TABLE `users` (
  `id` bigint NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `address` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `avatar_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `date_of_birth` datetime(6) DEFAULT NULL,
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `first_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `google_account_id` int DEFAULT NULL,
  `last_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `password` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `phone_number` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `status` enum('ACTIVE','BANNED','INACTIVE','UNVERIFIED','VERIFIED') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `role_id` bigint DEFAULT NULL,
  `account_balance` bigint DEFAULT '0',
  `is_active` tinyint(1) DEFAULT NULL,
  `is_subscription` tinyint(1) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `users`
--

INSERT INTO `users` (`id`, `created_at`, `updated_at`, `address`, `avatar_url`, `date_of_birth`, `email`, `first_name`, `google_account_id`, `last_name`, `password`, `phone_number`, `status`, `role_id`, `account_balance`, `is_active`, `is_subscription`) VALUES
(1, '2024-09-22 06:56:15.470639', '2024-09-22 06:56:15.470656', 'Ha Tinh, Viet Nam', 'https://www.shutterstock.com/image-vector/default-avatar-profile-icon-social-600nw-1677509740.jpg', '1979-10-13 00:00:00.000000', 'vy@gmail.com', 'Vy', 0, 'Ky Nguyen', '$2a$10$FGoLrPd5YESJhA7iiE3Jf.vnZU1BcxR3N5xtMxOJMKUjknCQ.iK7O', NULL, 'UNVERIFIED', 2, 0, 1, NULL),
(2, '2024-09-22 06:57:39.672269', '2024-09-22 06:57:39.672273', 'Ha Nam, Viet Nam', 'https://www.shutterstock.com/image-vector/default-avatar-profile-icon-social-600nw-1677509740.jpg', '1979-10-13 00:00:00.000000', 'baovu@gmail.com', 'Bao', 0, 'Van Vu', '$2a$10$rP7CU.wmF3SaP4lzNHNA6uUQr27JBAJr2Uoot1toTKmRlmGWcg7su', NULL, 'UNVERIFIED', 2, 0, 1, NULL),
(3, '2024-09-22 06:57:57.861037', '2024-09-22 06:57:57.861041', 'Phu Tho, Viet Nam', 'https://www.shutterstock.com/image-vector/default-avatar-profile-icon-social-600nw-1677509740.jpg', '1979-10-13 00:00:00.000000', 'manhtran@gmail.com', 'Tran', 0, 'Manh Duc', '$2a$10$nGC/NyBlxwFZzA0Ecj7gP.tYPRIvvFb8DZNNlaM/nylbvU3IzBWGi', NULL, 'UNVERIFIED', 2, 0, 1, NULL),
(4, '2024-09-22 06:58:19.527888', '2024-09-22 06:58:19.527892', 'Quang Binh, Viet Nam', 'https://www.shutterstock.com/image-vector/default-avatar-profile-icon-social-600nw-1677509740.jpg', '1979-10-13 00:00:00.000000', 'son@gmail.com', 'Son', 0, 'Cong Duong', '$2a$10$Orep8r6WJo5EjwNq7HyTL.kD8xDlwD5QA6Qk2HVQzkVjUXG1ccGPm', NULL, 'UNVERIFIED', 1, 0, 1, NULL),
(5, '2024-09-22 06:58:39.749509', '2024-09-22 06:58:39.749513', 'Tam Ki, Viet Nam', 'https://www.shutterstock.com/image-vector/default-avatar-profile-icon-social-600nw-1677509740.jpg', '1979-10-13 00:00:00.000000', 'duong@gmail.com', 'Duong', 0, 'Manh Nguyen', '$2a$10$Rg7wfZ2yabht3eBqhoJQxedR.r4ojQUGm9g9Jz5WtDJw3pWqpoiTu', NULL, 'UNVERIFIED', 1, 0, 1, NULL),
(6, '2024-09-22 06:59:14.042883', '2024-09-22 06:59:14.042887', 'Arab Saudi', 'https://www.shutterstock.com/image-vector/default-avatar-profile-icon-social-600nw-1677509740.jpg', '1979-10-13 00:00:00.000000', 'hoangclw@gmail.com', 'Hoang', 0, 'Luu Cao', '$2a$10$W22x0wOuJ5aFtLI1NvN1tO2ZgZhNAq0pEKdLGjsSgSKqJ462qitsO', NULL, 'UNVERIFIED', 4, 0, 1, NULL),
(7, '2024-09-22 06:59:36.244230', '2024-09-27 01:12:20.853127', 'Tokyo', 'https://auctionkoi.com/images/nnd-logo.png', '1979-10-13 00:00:00.000000', 'nnd@gmail.com', 'NND', 0, 'Farm', '$2a$10$DkiD8VzBXW5pGgGH2cYvQOtT3DbtLsuKHJ9.KjoN.GsI6LSQZJmui', NULL, 'VERIFIED', 3, 0, 1, NULL),
(8, '2024-09-22 06:59:53.747779', '2024-09-27 01:23:26.819524', 'Tokyo', 'https://auctionkoi.com/images/marushin-logo.png', '1979-10-13 00:00:00.000000', 'marushin@gmail.com', 'Marushin', 0, 'Farm', '$2a$10$sornl4RV.DkqkAS68ryyw.azD20A/4KmeeOa2IiTRxuS89nUQXMK.', NULL, 'VERIFIED', 3, 0, 1, NULL),
(9, '2024-09-22 07:00:02.941910', '2024-09-27 01:23:47.658909', 'Tokyo', 'https://auctionkoi.com/images/sakai-logo.png', '1979-10-13 00:00:00.000000', 'sakai@gmail.com', 'Sakai', 0, 'Farm', '$2a$10$SecO0EZQEWjBVyok7D7eTeBuVelmz7pjiCA8sddztylvVq801fiRu', NULL, 'VERIFIED', 3, 0, 1, NULL),
(10, '2024-09-22 07:00:12.510595', '2024-09-27 01:24:15.330550', 'Tokyo', 'https://auctionkoi.com/images/isa-logo.png', '1979-10-13 00:00:00.000000', 'isa@gmail.com', 'Isa', 0, 'Farm', '$2a$10$.Wj6q5N.bCR.3dFi3fc2dugPIrA0X91FiibIW/8Kw5mVyYAYjpLei', NULL, 'VERIFIED', 3, 0, 1, NULL),
(11, '2024-09-22 07:00:24.623763', '2024-09-27 01:24:49.064092', 'Tokyo', 'https://auctionkoi.com/images/maruhiro-logo.png', '1979-10-13 00:00:00.000000', 'maruhiro@gmail.com', 'Maruhiro', 0, 'Farm', '$2a$10$3TSjJG7bHVlNRdQAi5qBZeeXbLoNhMMXrsFp6FJcC9CFBmrjiV2Su', NULL, 'VERIFIED', 3, 0, 1, NULL),
(12, '2024-09-22 07:00:39.043899', '2024-09-27 01:25:04.915762', 'Tokyo', 'https://auctionkoi.com/images/torazo-logo.png', '1979-10-13 00:00:00.000000', 'torazo@gmail.com', 'Torazo', 0, 'Farm', '$2a$10$8sFYFH/3KtoCaV5wiLGhQecY7SwhM.gqFa9G2C1QrC0v2dQaNA0VK', NULL, 'VERIFIED', 3, 0, 1, NULL),
(13, '2024-09-22 07:00:50.485396', '2024-09-27 01:25:20.130483', 'Tokyo', 'https://auctionkoi.com/images/shinoda-logo.png', '1979-10-13 00:00:00.000000', 'shinoda@gmail.com', 'Shinoda', 0, 'Farm', '$2a$10$xGxH5nAWUFgWdo0Twsuc8.6PFhf6ZdgqrvAweQA/kmCj6z9PQKpQi', NULL, 'VERIFIED', 3, 0, 1, NULL),
(14, '2024-09-22 12:50:53.030997', '2024-09-27 01:27:52.862485', 'Tokyo', 'https://auctionkoi.com/images/kanno-logo.png', NULL, 'kanno@gmail.com', 'Kanno', 0, 'Farm', '$2a$10$.k6HZRqVtl7sxRtsgjiQ/eOjTpuy4pe7ZwpQKwgA9KwR70WFfeefq', NULL, 'VERIFIED', 3, 0, 1, NULL),
(15, '2024-09-22 12:52:28.527956', '2024-09-22 12:52:28.527967', '', 'https://www.shutterstock.com/image-vector/default-avatar-profile-icon-social-600nw-1677509740.jpg', NULL, 'mnhw.0612@gmail.com', 'Minh', 0, 'Nhu', '$2a$10$Fr4Khr9gT8rBlSlZIF3Z3.t62ZenOSFVJamccBe5yxLB27SSvF0Ru', NULL, NULL, 1, 0, 1, NULL),
(16, '2024-09-27 01:29:07.895187', '2024-09-27 01:32:19.992118', 'Tokyo', 'https://auctionkoi.com/images/dainichi-logo.png', NULL, 'dainichi@gmail.com', 'Dainichi', 0, 'Farm', '$2a$10$XckSkz8ZTZcOGOndc8YTduaPyPdHjj9hQ4PYezMAziSaUTsOgofQO', NULL, 'VERIFIED', 3, 0, 1, NULL),
(17, '2024-09-27 01:31:44.803419', '2024-09-27 01:32:22.979260', 'Tokyo', 'https://auctionkoi.com/images/omosako-logo.png', NULL, 'omosako@gmail.com', 'Omosako', 0, 'Farm', '$2a$10$CHmNC2psYO9xeJjzmbSiSe5yAlZzgl5rAN0RYibVMRyARj4YU9ve6', NULL, 'VERIFIED', 3, 0, 1, NULL),
(18, '2024-09-27 01:32:46.643498', '2024-09-27 01:33:56.144482', 'Tokyo', 'https://auctionkoi.com/images/izumiya-logo.png', NULL, 'izumiya@gmail.com', 'Izumiya', 0, 'Farm', '$2a$10$iWrH3sir7RQPmn0UHdV0fO5OiLzsVpNtS/u6drxxK1aEilMkycU9S', NULL, 'VERIFIED', 3, 0, 1, NULL),
(19, '2024-09-27 01:33:11.046544', '2024-09-27 01:33:58.113222', 'Tokyo', 'https://auctionkoi.com/images/marudo-logo.png', NULL, 'marudo@gmail.com', 'Marudo', 0, 'Farm', '$2a$10$OJa7MR5d3mG6Ju7Bl4NYqO8EOGRDcVzv.GD6m9NtSRK.AsFhakqCO', NULL, 'VERIFIED', 3, 0, 1, NULL),
(20, '2024-09-27 01:33:21.593383', '2024-09-27 01:33:59.833681', 'Tokyo', 'https://auctionkoi.com/images/marujyu-logo.png', NULL, 'marujyu@gmail.com', 'Marujyu', 0, 'Farm', '$2a$10$kxEegAgSjIy6yBPcU3Low.Xn9mamaOiU8mnzfehvZ7ltDy4R2ym1a', NULL, 'VERIFIED', 3, 0, 1, NULL),
(21, '2024-09-27 01:33:32.619792', '2024-09-27 01:34:01.592871', 'Tokyo', 'https://auctionkoi.com/images/shintaro-logo.png', NULL, 'shintaro@gmail.com', 'Shintaro', 0, 'Farm', '$2a$10$nbKQCdjMw1pEplr.o8ELdOudQWUDtImtQDAfwCQUzsMs8hF6scurW', NULL, 'VERIFIED', 3, 0, 1, NULL),
(22, '2024-09-27 15:21:18.973575', '2024-09-27 15:21:18.973595', 'Hoa Khanh, Da Nang', NULL, '2004-10-14 00:00:00.000000', 'hoangdz1604@gmail.com', 'Bao', 0, 'Chau', '$2a$10$jX.17d38A.AXWWJedIQ4p..KzZkKt.Pk3bwpU5gtPJLJtzygdjoy.', NULL, NULL, 1, 0, 0, NULL),
(23, '2024-09-27 15:25:59.286318', '2024-09-27 15:25:59.286337', 'Hoa Vang, Da Nang', NULL, '2004-10-14 00:00:00.000000', 'luuhai@gmail.com', 'Hai', 0, 'Luu', '$2a$10$SwG2quSV3SQNdAfKWpZd0uzaB9XwoaBfR8n27D9QptspytMyNkj3u', NULL, NULL, 1, 0, 0, NULL),
(24, '2024-09-27 15:28:51.559247', '2024-09-27 15:28:51.559286', 'Hoa Vang, Da Nang', NULL, '2004-10-14 00:00:00.000000', 'luuhaivn@gmail.com', 'Hai', 0, 'Luu', '$2a$10$MmAaEStq3KT7fRhKWlD8FOXL6GdTk1e.5BjPISpZunxAxCq5pGbwq', NULL, 'UNVERIFIED', 1, 0, 0, NULL),
(25, '2024-09-27 15:30:52.640808', '2024-09-27 15:30:52.640833', 'Hoa Vang, Da Nang', 'https://www.shutterstock.com/image-vector/default-avatar-profile-icon-social-600nw-1677509740.jpg', '2004-10-14 00:00:00.000000', 'lch@gmail.com', 'Hoang', 0, 'Hai', '$2a$10$63eAnEyqY4Y3xsktquBzJ.u39j/X8b.1NcafBdXSvx7Y0wai1hIxm', NULL, 'UNVERIFIED', 1, 0, 0, NULL),
(26, '2024-09-28 01:42:59.705789', '2024-09-28 01:42:59.705833', 'Vinh Long', 'https://www.shutterstock.com/image-vector/default-avatar-profile-icon-social-600nw-1677509740.jpg', '2004-10-14 00:00:00.000000', 'mah@gmail.com', 'Mac', 0, 'Hao Anh', '$2a$10$lwZzBtJFAJjPGRCJvJKlk.Gyd3RhLsV4eFai1BGIQByQjfjAj46xa', NULL, 'UNVERIFIED', 1, 0, 0, NULL),
(27, '2024-09-28 02:04:20.784604', '2024-09-28 02:04:20.784678', 'Vinh Long', 'https://www.shutterstock.com/image-vector/default-avatar-profile-icon-social-600nw-1677509740.jpg', '2004-10-14 00:00:00.000000', 'test@gmail.com', 'Mac', 0, 'Hao Anh', '$2a$10$M3WCdcgsRAW.zfCyf2kQwODnDFHm0fCExajXE74gR8Dq4WJZvGN2O', NULL, 'UNVERIFIED', 1, 0, 1, 1),
(28, '2024-09-28 02:09:21.078020', '2024-09-28 02:09:21.078121', 'Vinh Long', 'https://www.shutterstock.com/image-vector/default-avatar-profile-icon-social-600nw-1677509740.jpg', '2004-10-14 00:00:00.000000', 'test2@gmail.com', 'Mac', 0, 'Hao Anh', '$2a$10$pDVmvBj9Bs7/A8OVQ5nUzeQoCE4F95CU3ggA88cxHTveueEtpjvTe', NULL, 'UNVERIFIED', 1, 0, 1, 0),
(29, '2024-09-28 02:15:37.103176', '2024-09-28 02:15:37.103200', 'Vinh Long', 'https://www.shutterstock.com/image-vector/default-avatar-profile-icon-social-600nw-1677509740.jpg', '2004-10-14 00:00:00.000000', 'test3@gmail.com', 'Mac', 0, 'Hao Anh', '$2a$10$4evUxJ397YXsiexQ7yTMC.67GpQ9lSBCda4DvxA2aYQ833XCB89fW', NULL, 'UNVERIFIED', 1, 0, 1, 1);

--
-- Chỉ mục cho các bảng đã đổ
--

--
-- Chỉ mục cho bảng `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKp56c1712k691lhsyewcssf40f` (`role_id`);

--
-- AUTO_INCREMENT cho các bảng đã đổ
--

--
-- AUTO_INCREMENT cho bảng `users`
--
ALTER TABLE `users`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=30;

--
-- Các ràng buộc cho các bảng đã đổ
--

--
-- Các ràng buộc cho bảng `users`
--
ALTER TABLE `users`
  ADD CONSTRAINT `FKp56c1712k691lhsyewcssf40f` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
