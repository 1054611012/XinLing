<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast, showLoadingToast, closeToast, showConfirmDialog } from 'vant'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const nickname = ref('')
const gender = ref(0)
const birthday = ref('')
const saving = ref(false)
const showGenderPicker = ref(false)
const showDatePicker = ref(false)
const minDate = new Date(1950, 0, 1)
const maxDate = new Date(2010, 11, 31)

const genders = [
  { text: '保密', value: 0 },
  { text: '男', value: 1 },
  { text: '女', value: 2 }
]

function initData() {
  const info = authStore.userInfo
  if (info) {
    nickname.value = info.nickname || ''
    gender.value = info.gender ?? 0
    birthday.value = info.birthday || ''
  }
}

function onGenderConfirm({ selectedValues }: any) {
  gender.value = selectedValues[0]
  showGenderPicker.value = false
}

function onDateConfirm({ selectedValues }: { selectedValues: string[] }) {
  const [year, month, day] = selectedValues
  birthday.value = `${year}-${String(month).padStart(2, '0')}-${String(day).padStart(2, '0')}`
  showDatePicker.value = false
}

async function handleSave() {
  if (!nickname.value.trim()) {
    showToast('请输入昵称')
    return
  }
  saving.value = true
  const toast = showLoadingToast({ message: '保存中...', forbidClick: true, duration: 0 })
  try {
    await authStore.updateProfile({
      nickname: nickname.value.trim(),
      gender: gender.value,
      birthday: birthday.value
    })
    closeToast()
    showToast('保存成功')
    router.back()
  } catch {
    closeToast()
    showToast('保存失败')
  }
}

onMounted(() => { initData() })
</script>

<template>
  <div class="page profile-edit-page">
    <van-nav-bar title="编辑资料" left-arrow @click-left="router.back()" />

    <div class="edit-content">
      <!-- Avatar -->
      <div class="avatar-section">
        <div class="avatar-wrapper">
          <van-image
            round
            width="80"
            height="80"
            :src="authStore.userInfo?.avatar || ''"
          />
          <div class="avatar-overlay">
            <van-icon name="photograph" size="24" color="white" />
          </div>
        </div>
        <div class="avatar-hint">点击更换头像</div>
      </div>

      <!-- Form -->
      <van-cell-group :border="false" class="form-group">
        <van-field
          v-model="nickname"
          label="昵称"
          placeholder="请输入昵称"
          maxlength="20"
          clearable
          input-align="right"
        />
        <van-cell
          title="性别"
          is-link
          :value="genders.find(g => g.value === gender)?.text || '保密'"
          @click="showGenderPicker = true"
        />
        <van-cell
          title="生日"
          is-link
          :value="birthday || '请选择'"
          @click="showDatePicker = true"
        />
      </van-cell-group>

      <!-- Save -->
      <div class="save-section">
        <van-button
          round
          block
          type="primary"
          :loading="saving"
          @click="handleSave"
        >
          保存
        </van-button>
      </div>
    </div>

    <!-- Gender Picker -->
    <van-action-sheet v-model:show="showGenderPicker" title="选择性别">
      <van-picker
        :columns="genders"
        @confirm="onGenderConfirm"
        @cancel="showGenderPicker = false"
      />
    </van-action-sheet>

    <!-- Date Picker -->
    <van-action-sheet v-model:show="showDatePicker" title="选择生日">
      <van-date-picker
        :min-date="minDate"
        :max-date="maxDate"
        @confirm="onDateConfirm"
        @cancel="showDatePicker = false"
        title=""
      />
    </van-action-sheet>
  </div>
</template>

<style scoped>
.profile-edit-page {
  min-height: 100vh;
  min-height: 100dvh;
}

.edit-content {
  padding: 24px 16px;
}

.avatar-section {
  text-align: center;
  margin-bottom: 32px;
}

.avatar-wrapper {
  position: relative;
  display: inline-block;
  cursor: pointer;
}

.avatar-overlay {
  position: absolute;
  inset: 0;
  border-radius: 50%;
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s;
}

.avatar-wrapper:hover .avatar-overlay,
.avatar-wrapper:active .avatar-overlay {
  opacity: 1;
}

.avatar-hint {
  font-size: 12px;
  color: var(--app-text-secondary);
  margin-top: 8px;
}

.form-group {
  border-radius: 12px;
  overflow: hidden;
}

.form-group :deep(.van-cell) {
  background: var(--app-bg-card);
}

.form-group :deep(.van-field__label) {
  color: var(--app-text-primary);
  width: 60px;
}

.save-section {
  margin-top: 32px;
}
</style>
