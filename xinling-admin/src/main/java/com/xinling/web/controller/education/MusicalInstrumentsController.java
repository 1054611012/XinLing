package com.xinling.web.controller.education;
//
//import java.util.List;
//import javax.servlet.http.HttpServletResponse;
//
//import com.xinling.education.domain.MusicalInstruments;
//import com.xinling.education.service.IMusicalInstrumentsService;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.PutMapping;
//import org.springframework.web.bind.annotation.DeleteMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//import com.xinling.common.annotation.Log;
//import com.xinling.common.core.controller.BaseController;
//import com.xinling.common.core.domain.AjaxResult;
//import com.xinling.common.enums.BusinessType;
//import com.xinling.common.utils.poi.ExcelUtil;
//import com.xinling.common.core.page.TableDataInfo;
//
///**
// * 乐器信息Controller
// *
// * @author xinling
// * @date 2025-07-31
// */
//@RestController
//@RequestMapping("/education/instruments")
//public class MusicalInstrumentsController extends BaseController
//{
//    @Autowired
//    private IMusicalInstrumentsService musicalInstrumentsService;
//
//    /**
//     * 查询乐器信息列表
//     */
//    @PreAuthorize("@ss.hasPermi('education:instruments:list')")
//    @GetMapping("/list")
//    public TableDataInfo list(MusicalInstruments musicalInstruments)
//    {
//        startPage();
//        List<MusicalInstruments> list = musicalInstrumentsService.selectMusicalInstrumentsList(musicalInstruments);
//        return getDataTable(list);
//    }
//
//    /**
//     * 导出乐器信息列表
//     */
//    @PreAuthorize("@ss.hasPermi('education:instruments:export')")
//    @Log(title = "乐器信息", businessType = BusinessType.EXPORT)
//    @PostMapping("/export")
//    public void export(HttpServletResponse response, MusicalInstruments musicalInstruments)
//    {
//        List<MusicalInstruments> list = musicalInstrumentsService.selectMusicalInstrumentsList(musicalInstruments);
//        ExcelUtil<MusicalInstruments> util = new ExcelUtil<MusicalInstruments>(MusicalInstruments.class);
//        util.exportExcel(response, list, "乐器信息数据");
//    }
//
//    /**
//     * 获取乐器信息详细信息
//     */
//    @PreAuthorize("@ss.hasPermi('education:instruments:query')")
//    @GetMapping(value = "/{id}")
//    public AjaxResult getInfo(@PathVariable("id") String id)
//    {
//        return success(musicalInstrumentsService.selectMusicalInstrumentsById(id));
//    }
//
//    /**
//     * 新增乐器信息
//     */
//    @PreAuthorize("@ss.hasPermi('education:instruments:add')")
//    @Log(title = "乐器信息", businessType = BusinessType.INSERT)
//    @PostMapping
//    public AjaxResult add(@RequestBody MusicalInstruments musicalInstruments)
//    {
//        return toAjax(musicalInstrumentsService.insertMusicalInstruments(musicalInstruments));
//    }
//
//    /**
//     * 修改乐器信息
//     */
//    @PreAuthorize("@ss.hasPermi('education:instruments:edit')")
//    @Log(title = "乐器信息", businessType = BusinessType.UPDATE)
//    @PutMapping
//    public AjaxResult edit(@RequestBody MusicalInstruments musicalInstruments)
//    {
//        return toAjax(musicalInstrumentsService.updateMusicalInstruments(musicalInstruments));
//    }
//
//    /**
//     * 删除乐器信息
//     */
//    @PreAuthorize("@ss.hasPermi('education:instruments:remove')")
//    @Log(title = "乐器信息", businessType = BusinessType.DELETE)
//	@DeleteMapping("/{ids}")
//    public AjaxResult remove(@PathVariable String[] ids)
//    {
//        return toAjax(musicalInstrumentsService.deleteMusicalInstrumentsByIds(ids));
//    }
//}
