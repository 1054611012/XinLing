package com.xinling.psyc.domain.vo;

import com.xinling.psyc.domain.PsycOptions;
import com.xinling.psyc.domain.PsycQuestions;

import java.util.List;

/**
 * @author SuXia
 * @date 2025/11/25 16:16
 */
public class PsycQuestionsVo extends PsycQuestions {

    private List<PsycOptions> psycOptionsList;

    @Override
    public List<PsycOptions> getPsycOptionsList() {
        return psycOptionsList;
    }

    @Override
    public void setPsycOptionsList(List<PsycOptions> psycOptionsList) {
        this.psycOptionsList = psycOptionsList;
    }
}
