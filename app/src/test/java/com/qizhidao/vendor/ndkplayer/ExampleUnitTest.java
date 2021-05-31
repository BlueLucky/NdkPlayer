package com.qizhidao.vendor.ndkplayer;

import android.os.IInterface;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testMaxPrice() {
        //[7,1,5,3,6,4]
//        int []prices = {7,1,5,3,6,4};
//        int []prices = {7,6,5,4,3,2};
//        int []prices = {1,2,3,4,5,6};
//        int []prices = {2,1,4,5,2,9,7};
//        maxProfit(prices);

//        int []prices = {1,2,3,4,5,6,7};
//        rotate(prices,3);
//        System.out.print("[");
//        for (int value:prices){
//            System.out.print(value+",");
//        }
//        System.out.print("]");
//        int singleNumber[] = {9,8,1,0,9};
//        System.out.println("number:"+singleNumber(singleNumber));
//        int result[] = plusOne2(singleNumber);
//[0,1,0,3,12]
//        int singleNumber[] = {0,1,0,3,12};
//        moveZeroes(singleNumber);
//        System.out.print("[");
//        for (int value : singleNumber) {
//            System.out.print(value + ",");
//        }
//        System.out.print("]");

        //        char[][] board = {
        //                {'5', '3', '.', '.', '7', '.', '.', '.', '.'},
        //                {'6', '.', '.', '1', '9', '5', '.', '.', '.'},
        //                {'.', '9', '8', '.', '.', '.', '.', '6', '.'},
        //                {'8', '.', '.', '.', '6', '.', '.', '.', '3'},
        //                {'4', '.', '.', '8', '.', '3', '.', '.', '1'},
        //                {'7', '.', '.', '.', '2', '.', '.', '.', '6'},
        //                {'.', '6', '.', '.', '.', '.', '2', '8', '.'},
        //                {'.', '.', '.', '4', '1', '9', '.', '.', '5'},
        //                {'.', '.', '.', '.', '8', '.', '.', '7', '9'}
        //        };
        //        System.out.println("isValidSudoku:" + isValidSudoku(board));

        int[][] max = {{1, 2, 3}, {4, 5, 6}, {7, 8, 9}};
        rotate(max);
        for (int[] buff : max) {
            System.out.print("[");
            for (int value : buff) {
                System.out.print(value + ",");
            }
            System.out.print("]");
            System.out.println();
        }
    }

    public void rotate(int[][] matrix) {
        for (int[] row : matrix) {
            reverse(row, 0, row.length - 1);
        }
        int traverseSize = matrix.length - 1;
        int traverseColSize = matrix.length;

        int changeCol = matrix.length - 1;

        for (int i = 0; i < traverseSize; i++) {
            for (int j = 0; j < traverseColSize; j++) {
                int temp = matrix[i][j];
                matrix[i][j] = matrix[changeCol + i - j][changeCol];
                matrix[changeCol + i - j][changeCol] = temp;
            }
            changeCol--;
            traverseColSize--;
        }
    }

    //数独
    public boolean isValidSudoku(char[][] board) {
        int rowLength = board.length;
        int cloumLength = board[0].length;
        Set<Integer> rowChas = new HashSet<>();
        Set<Integer> cloumChas = new HashSet<>();

        Set<Integer> firstBlock = new HashSet<>();
        Set<Integer> secondBlock = new HashSet<>();
        Set<Integer> thirdBlock = new HashSet<>();

        for (int i = 0; i < rowLength; i++) {
            for (int j = 0; j < cloumLength; j++) {
                //计算一行数据是否有相同的
                if (board[i][j] != '.') {
                    int value = Character.getNumericValue(board[i][j]);
                    System.out.println("value:" + value);
                    if (!rowChas.add(value)) {
                        rowChas.clear();
                        return false;
                    }
                }
                if (board[j][i] != '.') {
                    if (!cloumChas.add(Character.getNumericValue(board[j][i]))) {
                        cloumChas.clear();
                        return false;
                    }
                }
                if (board[i][j] != '.') {
                    if (j / 3 == 0) {
                        if (!firstBlock.add(Character.getNumericValue(board[i][j]))) {
                            firstBlock.clear();
                            secondBlock.clear();
                            thirdBlock.clear();
                            return false;
                        }
                    } else if (j / 3 == 1) {
                        if (!secondBlock.add(Character.getNumericValue(board[i][j]))) {
                            firstBlock.clear();
                            secondBlock.clear();
                            thirdBlock.clear();
                            return false;
                        }
                    } else {
                        if (!thirdBlock.add(Character.getNumericValue(board[i][j]))) {
                            firstBlock.clear();
                            secondBlock.clear();
                            thirdBlock.clear();
                            return false;
                        }
                    }
                }
            }

            if (i % 3 == 2) {
                firstBlock.clear();
                secondBlock.clear();
                thirdBlock.clear();
            }
            rowChas.clear();
            cloumChas.clear();
        }
        return true;
    }

    public void moveZeroes(int[] nums) {
        int index = 0;
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] != 0) {
                nums[index++] = nums[i];
            }
        }
        while (index < nums.length) {
            nums[index++] = 0;
        }
    }

    public int[] plusOne2(int[] digits) {
        int length = digits.length;
        for (int i = length - 1; i >= 0; i--) {
            if (digits[i] == 9) {
                digits[i] = 0;
            } else {
                digits[i]++;
                return digits;
            }
        }
        int[] expandReuslt = new int[length + 1];
        expandReuslt[0] = 1;
        System.arraycopy(digits, 0, expandReuslt, 1, length);
        return expandReuslt;
    }

    public int[] plusOne(int[] digits) {
        double plusNum = 0;
        int srcLen = digits.length;
        for (int i = 0; i < srcLen; i++) {
            int mi = srcLen - 1 - i;
            double wei = Math.pow(10, mi) * digits[i];
            plusNum += wei;
        }

        int length = (int) (Math.log10(plusNum + 1) + 1);
        int[] result = new int[length];
        double plusOne = plusNum + 1;
        System.out.println("length:" + length);
        for (int i = 0; i < length; i++) {
            int mi = length - 1 - i;
            double wei = Math.pow(10, mi);
            int weiResult = (int) (plusOne / wei);
            result[i] = weiResult;
            plusOne = plusOne - (weiResult * wei);
        }
        return result;
    }


    public int[] intersect(int[] nums1, int[] nums2) {
        Arrays.sort(nums1);
        Arrays.sort(nums2);
        List<Integer> result = new ArrayList<Integer>();
        for (int i = 0, j = 0; i < nums1.length && j < nums2.length; ) {
            if (nums1[i] == nums2[j]) {
                result.add(nums1[i]);
                i++;
                j++;
            } else if (nums1[i] > nums2[j]) {
                j++;
            } else {
                i++;
            }
        }
        int[] arrayResult = new int[result.size()];
        int position = 0;
        for (Integer value : result) {
            arrayResult[position++] = value;
        }
        return arrayResult;
    }


    public int singleNumber(int[] nums) {
        Arrays.sort(nums);
        for (int i = 0; i < nums.length; i++) {
            if (i == 0) {
                if (nums[i] != nums[i + 1]) {
                    return nums[i];
                }
            } else if (i == nums.length - 1) {
                if (nums[i] != nums[i - 1]) {
                    return nums[i];
                }
            } else {
                if (!(nums[i] == nums[i - 1] || nums[i] == nums[i + 1])) {
                    return nums[i];
                }
            }
        }
        return -1;
    }

    public boolean containsDuplicate(int[] nums) {
        Set<Integer> set = new HashSet<Integer>();
        for (int value : nums) {
            if (!set.add(value)) {
                return true;
            }
        }
        return false;
    }


    public void rotate(int[] nums, int k) {
        reverse(nums, 0, nums.length - 1);
        reverse(nums, 0, k - 1);
        reverse(nums, k, nums.length - 1);
    }

    public void reverse(int[] nums, int start, int end) {
        while (start < end) {
            int temp = nums[start];
            nums[start] = nums[end];
            nums[end] = temp;
            start += 1;
            end -= 1;
        }
    }

    public void reverse(char[] nums, int start, int end) {
        while (start < end) {
            char temp = nums[start];
            nums[start] = nums[end];
            nums[end] = temp;
            start += 1;
            end -= 1;
        }
    }

    public int maxProfit(int[] prices) {
        int maxPrice = 0;
        int length = prices.length;
        for (int i = 0; i < length - 1; i++) {
            int buyPoint = -1;
            int selPoint = -1;
            if (prices[i] < prices[i + 1]) {
                buyPoint = i;
            }
            for (int j = i + 1; j < length - 1; j++) {
                if (prices[j] > prices[j + 1]) {
                    selPoint = j;
                    break;
                }
            }
            if (buyPoint != -1) {
                if (selPoint == -1) {
                    maxPrice += prices[prices.length - 1] - prices[buyPoint];
                    break;
                } else {
                    maxPrice += prices[selPoint] - prices[buyPoint];
                    i = selPoint;
                }
            }
            System.out.println("buyPoint:" + buyPoint + "  selPonit:" + selPoint);
        }
        System.out.println("maxPrice:" + maxPrice);
        return maxPrice;
    }

    @Test
    public void testString() {
        String[] strs = {"flower", "flow", "flight"};
        System.out.println("longestCommonPrefix:" + longestCommonPrefix(strs));
    }

    public String longestCommonPrefix(String[] strs) {
        if (strs.length <= 0) {
            return "";
        }
        if (strs.length == 1) {
            return strs[0];
        }
        int reLength = strs[0].length();
        String minStr = strs[0];
        for (int i = 1; i < strs.length; i++) {
            if (strs[i].length() < reLength) {
                reLength = strs[i].length();
                minStr = strs[i];
            }
        }
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < reLength; i++) {
            char compareChar = minStr.charAt(i);
            for (int j = 0; j < strs.length; j++) {
                if (compareChar != strs[j].charAt(i)) {
                    return result.toString();
                }
            }
            result.append(compareChar);
        }

        return result.toString();
    }

    public String countAndSay(int n) {
        String result = "1";
        for (int i = 1; i < n; i++) {
            int dffPosition = 0;
            StringBuilder tempResult = new StringBuilder();
            for (int j = 0; j < result.length(); j++) {
                int nextPosition = j + 1;
                if (nextPosition < result.length()) {
                    if (result.charAt(j) != result.charAt(nextPosition)) {
                        tempResult.append(j - dffPosition + 1).append(result.charAt(j));
                        dffPosition = nextPosition;
                    }
                } else {
                    tempResult.append(j - dffPosition + 1).append(result.charAt(j));
                }
            }
            result = tempResult.toString();
        }
        return result;
    }

    public int strStr(String haystack, String needle) {
        final int sourceLength = haystack.length();
        final int targetLength = needle.length();
        if (targetLength == 0) {
            return 0;
        }
        char first = needle.charAt(0);
        int max = sourceLength - targetLength;
        for (int i = 0; i <= max; i++) {
            //寻找第一个target元素
            if (haystack.charAt(i) != first) {
                while (++i <= max && haystack.charAt(i) != first) ;
            }
            //继续选择其他字符
            if (i <= max) {
                int j = i + 1;
                int end = j + targetLength - 1;
                for (int k = 1; j < end && haystack.charAt(j) == needle.charAt(k); j++, k++) ;
                if (j == end) {
                    return i;
                }
            }
        }
        return -1;
    }

    public int myAtoi(String s) {
        s = s.trim();
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < s.length(); i++) {
            char currentChar = s.charAt(i);
            if (currentChar == '-' || currentChar == '+') {
                if (i == 0) {
                    stringBuilder.append(currentChar);
                } else {
                    break;
                }
            } else if (Character.isDigit(currentChar)) {
                stringBuilder.append(currentChar);
            } else {
                break;
            }
        }
        String result = stringBuilder.toString();
        System.out.println("result:" + result);
        if (result.isEmpty()) {
            return 0;
        } else {
            return parseInt(result, 10);
        }
    }


    private int parseInt(String s, int radix) {
        int result = 0;
        boolean negative = false;
        int i = 0, len = s.length();
        int limit = -Integer.MAX_VALUE;
        int multmin;
        int digit;
        if (len > 0) {
            char firstChar = s.charAt(0);
            if (firstChar < '0') { // Possible leading "+" or "-"
                if (firstChar == '-') {
                    negative = true;
                    limit = Integer.MIN_VALUE;
                } else if (firstChar != '+') {
                    return 0;
                }
                if (len == 1) // Cannot have lone "+" or "-"
                    return 0;
                i++;
            }
            multmin = limit / radix;
            while (i < len) {
                // Accumulating negatively avoids surprises near MAX_VALUE
                digit = Character.digit(s.charAt(i++), radix);
                System.out.println("digit:" + digit);
                if (digit < 0) {
                    return 0;
                }
                if (result < multmin) {
                    return negative ? Integer.MIN_VALUE : Integer.MAX_VALUE;
                }
                result *= radix;
                if (result < limit + digit) {
                    return negative ? Integer.MIN_VALUE : Integer.MAX_VALUE;
                }
                result -= digit;
            }
        } else {
            return 0;
        }
        return negative ? result : -result;
    }

    public boolean isPalindrome(String s) {
        int left = 0;
        int right = s.length() - 1;
        s = s.toLowerCase();
        while (right - left >= 1) {
            if (!Character.isLetterOrDigit(s.charAt(left))) {
                left++;
                continue;
            }
            if (!Character.isLetterOrDigit(s.charAt(right))) {
                right--;
                continue;
            }
            if (s.charAt(left) != s.charAt(right)) {
                return false;
            }
            left++;
            right--;
        }
        return true;
    }

    public boolean isAnagram(String s, String t) {
        if (s.length() != t.length()) {
            return false;
        }
        char[] firstChars = s.toCharArray();
        char[] secondChars = t.toCharArray();
        int chars[] = new int[26];
        for (char c : firstChars) {
            chars[c - 'a']++;
        }
        for (char c : secondChars) {
            chars[c - 'a']--;
        }
        for (int c : chars) {
            if (c != 0) {
                return false;
            }
        }
        return true;
    }

    public int firstUniqChar(String s) {
        char[] chars = s.toCharArray();
        int intChars[] = new int[26];
        int indexs[] = new int[26];
        for (int i = 0; i < chars.length; i++) {
            int index = chars[i] - 'a';
            intChars[index]++;
            indexs[index] = i;
        }
        for (int i = 0; i < chars.length; i++) {
            int index = chars[i] - 'a';
            if (intChars[index] == 1) {
                return indexs[index];
            }
        }
        return 0;
    }

    public int reverse(int x) {
        long result = 0;
        while (x != 0) {
            result = result * 10 + x % 10;
            x = x / 10;
        }
        if (result < Integer.MIN_VALUE || result > Integer.MAX_VALUE) {
            return 0;
        }
        return (int) result;
    }

    public void reverseString(char[] s) {
        int condition = s.length;
        for (int i = 0; i < condition; i++) {
            if (i <= condition) {
                char temp = s[i];
                s[i] = s[condition - 1];
                s[condition - 1] = temp;

                condition--;
            }
        }
    }

    private void printStr(char[] chars) {
        System.out.println();
        System.out.print("[");
        for (char c : chars) {
            System.out.print(c + " ");
        }
        System.out.print("]");
    }

    @Test
    public void testNote() {
        ListNode head = new ListNode(1);
        ListNode firstNode = new ListNode(2);
        head.next = firstNode;

        ListNode secondeNode = new ListNode(3);
        firstNode.next = secondeNode;

        ListNode thirdNode = new ListNode(4);
        secondeNode.next = thirdNode;


        ListNode fourNode = new ListNode(5);
        thirdNode.next = fourNode;

        fourNode.next = null;

        ListNode resultNode = reverseList(head);

        System.out.println();
        System.out.print("[");
        System.out.print(resultNode.toString());
        System.out.print("]");
         System.out.println();
    }

    public ListNode mergeTwoLists(ListNode l1, ListNode l2) {
        ListNode curNote;
        
        return null;
    }


    public ListNode reverseList(ListNode head) {
      if(head==null||head.next==null){
          return head;
      }
      ListNode temp = head.next;
      ListNode newHead = reverseList(head.next);
      temp.next = head;
      head.next = null;
      return newHead;
    }

    public ListNode removeNthFromEnd(ListNode head, int n) {
        int count =1;
        ListNode rvNode = head;
        while (rvNode.next!=null){
            count++;
            rvNode = rvNode.next;
        }
        System.out.println("count："+count);
        ListNode tempNode = head;
        int removeIndex = count-n;
        if(removeIndex==0){
            return head.next;
        }
        System.out.println("removeIndex："+removeIndex);
        count = 1;
        while (tempNode.next!=null){
             if(removeIndex==count){
                 tempNode.next = tempNode.next.next;
                 break;
             }else{
                 tempNode = tempNode.next;
                 count++;
             }
        }
        return head;
    }
}