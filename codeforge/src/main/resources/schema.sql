-- ============================================
-- SCHEMA + SEED DATA
-- ============================================
-- Using CREATE TABLE IF NOT EXISTS to avoid conflicts with JPA

-- Users table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'ROLE_USER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_username (username),
    INDEX idx_email (email)
);

-- Problems table
CREATE TABLE IF NOT EXISTS problems (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description LONGTEXT NOT NULL,
    examples LONGTEXT,
    constraints LONGTEXT,
    difficulty VARCHAR(20) NOT NULL,
    tags VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    INDEX idx_difficulty (difficulty),
    INDEX idx_is_active (is_active),
    INDEX idx_created_at (created_at)
);

-- Testcases table
CREATE TABLE IF NOT EXISTS testcases (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    problem_id BIGINT NOT NULL,
    input LONGTEXT NOT NULL,
    expected_output LONGTEXT NOT NULL,
    is_hidden BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (problem_id) REFERENCES problems(id) ON DELETE CASCADE,
    INDEX idx_problem_id (problem_id),
    INDEX idx_is_hidden (is_hidden)
);

-- Submissions table
CREATE TABLE IF NOT EXISTS submissions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    problem_id BIGINT NOT NULL,
    code LONGTEXT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    score INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (problem_id) REFERENCES problems(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_problem_id (problem_id),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at)
);

-- Submission Results table
CREATE TABLE IF NOT EXISTS submission_results (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    submission_id BIGINT NOT NULL,
    testcase_id BIGINT NOT NULL,
    passed BOOLEAN NOT NULL,
    output LONGTEXT,
    execution_time INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (submission_id) REFERENCES submissions(id) ON DELETE CASCADE,
    FOREIGN KEY (testcase_id) REFERENCES testcases(id) ON DELETE CASCADE,
    INDEX idx_submission_id (submission_id),
    INDEX idx_testcase_id (testcase_id)
);

-- Behavioral Questions table
CREATE TABLE IF NOT EXISTS behavioral_questions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    question VARCHAR(500) NOT NULL,
    category VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    INDEX idx_is_active (is_active)
);

-- Behavioral Answers table
CREATE TABLE IF NOT EXISTS behavioral_answers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    question_id BIGINT NOT NULL,
    answer LONGTEXT NOT NULL,
    ai_score INT,
    feedback LONGTEXT,
    followup_question VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (question_id) REFERENCES behavioral_questions(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_question_id (question_id),
    INDEX idx_created_at (created_at)
);

-- ============================================
-- SEED DATA (Sample Problems)
-- ============================================

-- Insert sample problems (only if they don't exist)
INSERT IGNORE INTO problems (id, title, description, examples, constraints, difficulty, tags, is_active) VALUES

(1, 'Two Sum', 
'Given an array of integers nums and an integer target, return the indices of the two numbers that add up to target.\nYou may assume that each input has exactly one solution, and you may not use the same element twice.\nYou can return the answer in any order.',
'Example 1:\nInput: nums = [2,7,11,15], target = 9\nOutput: [0,1]\nExplanation: nums[0] + nums[1] == 9, so we return [0, 1].\n\nExample 2:\nInput: nums = [3,2,4], target = 6\nOutput: [1,2]\n\nExample 3:\nInput: nums = [3,3], target = 6\nOutput: [0,1]',
'2 <= nums.length <= 10^4\n-10^9 <= nums[i] <= 10^9\n-10^9 <= target <= 10^9\nOnly one valid answer exists.',
'EASY', 'array,hash-table,two-pointers', TRUE),

(2, 'Reverse String',
'Write a function that reverses a string. The input string is given as an array of characters s.\nYou must do this by modifying the input array in-place with O(1) extra memory.',
'Example 1:\nInput: s = ["h","e","l","l","o"]\nOutput: ["o","l","l","e","h"]\n\nExample 2:\nInput: s = ["H","a","n","n","a","h"]\nOutput: ["h","a","n","n","a","H"]',
'1 <= s.length <= 10^5\ns[i] is a printable ascii character.',
'EASY', 'string,two-pointers,recursion', TRUE),

(3, 'Longest Substring Without Repeating Characters',
'Given a string s, find the length of the longest substring without repeating characters.',
'Example 1:\nInput: s = "abcabcbb"\nOutput: 3\nExplanation: The answer is "abc", with the length of 3.\n\nExample 2:\nInput: s = "bbbbb"\nOutput: 1\nExplanation: The answer is "b", with the length of 1.\n\nExample 3:\nInput: s = "pwwkew"\nOutput: 3\nExplanation: The answer is "wke", with the length of 3.',
'0 <= s.length <= 5 * 10^4\ns consists of English letters, digits, symbols and spaces.',
'MEDIUM', 'string,sliding-window,hash-table', TRUE),

(4, 'Binary Tree Level Order Traversal',
'Given the root of a binary tree, return the level order traversal of its nodes\' values. (i.e., from left to right, level by level).',
'Example 1:\nInput: root = [3,9,20,null,null,15,7]\nOutput: [[3],[9,20],[15,7]]\n\nExample 2:\nInput: root = [1]\nOutput: [[1]]',
'The number of nodes in the tree is in the range [0, 2000].\n-1000 <= Node.val <= 1000',
'MEDIUM', 'tree,breadth-first-search,queue', TRUE),

(5, 'Median of Two Sorted Arrays',
'Given two sorted arrays nums1 and nums2 of size m and n respectively, return the median of the two sorted arrays.\nThe overall run time complexity should be O(log (m+n)).',
'Example 1:\nInput: nums1 = [1,3], nums2 = [2]\nOutput: 2.00000\nExplanation: merged array = [1,2,3] and median is 2.\n\nExample 2:\nInput: nums1 = [1,2], nums2 = [3,4]\nOutput: 2.50000\nExplanation: merged array = [1,2,3,4] and median is (2 + 3) / 2 = 2.5.',
'nums1.length == m\nnums2.length == n\n0 <= m <= 1000\n0 <= n <= 1000\n0 <= m + n <= 2000\n-10^6 <= nums1[i], nums2[i] <= 10^6',
'HARD', 'array,binary-search,divide-and-conquer', TRUE);

-- Insert sample testcases for Problem 1 (Two Sum)
INSERT IGNORE INTO testcases (id, problem_id, input, expected_output, is_hidden) VALUES
(1, 1, '[2,7,11,15]\n9', '[0,1]', FALSE),
(2, 1, '[3,2,4]\n6', '[1,2]', FALSE),
(3, 1, '[3,3]\n6', '[0,1]', FALSE),
(4, 1, '[1,2,3,4,5]\n9', '[3,4]', TRUE);

-- Insert sample testcases for Problem 2 (Reverse String)
INSERT IGNORE INTO testcases (id, problem_id, input, expected_output, is_hidden) VALUES
(5, 2, '"hello"', '"olleh"', FALSE),
(6, 2, '"a"', '"a"', FALSE),
(7, 2, '"ab"', '"ba"', FALSE),
(8, 2, '"abcdef"', '"fedcba"', TRUE);

-- Insert sample testcases for Problem 3 (Longest Substring)
INSERT IGNORE INTO testcases (id, problem_id, input, expected_output, is_hidden) VALUES
(9, 3, '"abcabcbb"', '3', FALSE),
(10, 3, '"bbbbb"', '1', FALSE),
(11, 3, '"pwwkew"', '3', FALSE),
(12, 3, '""', '0', TRUE);

-- Insert sample testcases for Problem 4 (Binary Tree Level Order)
INSERT IGNORE INTO testcases (id, problem_id, input, expected_output, is_hidden) VALUES
(13, 4, '[3,9,20,null,null,15,7]', '[[3],[9,20],[15,7]]', FALSE),
(14, 4, '[1]', '[[1]]', FALSE),
(15, 4, '[]', '[]', TRUE);

-- Insert sample testcases for Problem 5 (Median of Two Sorted Arrays)
INSERT IGNORE INTO testcases (id, problem_id, input, expected_output, is_hidden) VALUES
(16, 5, '[1,3]\n[2]', '2.00000', FALSE),
(17, 5, '[1,2]\n[3,4]', '2.50000', FALSE),
(18, 5, '[0,0]\n[0,0]', '0.00000', TRUE);

-- Insert sample behavioral questions
INSERT IGNORE INTO behavioral_questions (id, question, category, is_active) VALUES
(1, 'Tell me about yourself', 'Introduction', TRUE),
(2, 'What are your strengths?', 'Self-Assessment', TRUE),
(3, 'Describe a time when you had to solve a difficult problem', 'Problem Solving', TRUE),
(4, 'How do you handle feedback and criticism?', 'Communication', TRUE),
(5, 'Tell me about a time when you worked in a team', 'Teamwork', TRUE),
(6, 'What is your biggest weakness?', 'Self-Awareness', TRUE),
(7, 'Why do you want to work for our company?', 'Motivation', TRUE),
(8, 'Describe a time when you showed leadership', 'Leadership', TRUE);