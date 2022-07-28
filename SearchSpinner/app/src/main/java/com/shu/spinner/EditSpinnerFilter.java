package com.shu.spinner;

/**
 * @Author shu
 * @Version 1.0
 * @Date: 2022/07/27/ 16:22
 * @Description 监听
 **/
public interface EditSpinnerFilter {
    /**
     * editText输入监听
     * @param keyword
     * @return
     */
    boolean onFilter(String keyword);
}
