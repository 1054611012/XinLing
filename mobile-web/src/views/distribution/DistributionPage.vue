<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { showToast, showLoadingToast, closeToast, showConfirmDialog } from 'vant'
import { getDistributorInfo, getCommissionRecords, getTeamList, applyWithdraw } from '@/api/distribution'
import type { DistributorInfoVO, CommissionRecord, TeamMember } from '@/api/distribution'
import { DEFAULT_AVATAR } from '@/utils/constants'

const distributorInfo = ref<DistributorInfoVO | null>(null)
const commissionRecords = ref<CommissionRecord[]>([])
const teamList = ref<TeamMember[]>([])
const loading = ref(false)
const withdrawAmount = ref(0)

async function loadData() {
  loading.value = true
  try {
    const [infoRes, commRes, teamRes] = await Promise.all([
      getDistributorInfo(),
      getCommissionRecords(1, 20),
      getTeamList(1, 20)
    ])
    distributorInfo.value = infoRes.data
    commissionRecords.value = (commRes.data as any).list || commRes.data || []
    teamList.value = Array.isArray(teamRes.data) ? teamRes.data : []
  } catch {
    showToast('加载失败')
  } finally {
    loading.value = false
  }
}

function copyCode() {
  if (!distributorInfo.value?.promotionCode) return
  navigator.clipboard.writeText(distributorInfo.value.promotionCode).then(() => {
    showToast('已复制推广码')
  }).catch(() => {
    showToast('复制失败，请手动复制')
  })
}

async function handleWithdraw() {
  if (!distributorInfo.value) return
  if (distributorInfo.value.availableCommission <= 0) {
    showToast('无可提现金额')
    return
  }
  try {
    await showConfirmDialog({
      title: '申请提现',
      message: `确定要提现 ¥${distributorInfo.value.availableCommission} 吗？`,
      confirmButtonColor: '#7c5cff'
    })
    const toast = showLoadingToast({
      message: '提交中...',
      forbidClick: true,
      duration: 0
    })
    await applyWithdraw(distributorInfo.value.availableCommission, 'alipay')
    closeToast()
    showToast('提现申请已提交')
    loadData()
  } catch {
    // cancelled
  }
}

function formatAmount(amount: number): string {
  return '¥' + (amount / 100).toFixed(2)
}

onMounted(() => {
  loadData()
})
</script>

<template>
  <div class="page distribution-page">
    <!-- Overview Card -->
    <div class="overview-card glass-card">
      <div class="overview-title">推广中心</div>
      <div class="stats-grid">
        <div class="stat-item">
          <div class="stat-value gradient-text">{{ distributorInfo ? formatAmount(distributorInfo.totalCommission) : '--' }}</div>
          <div class="stat-label">累计佣金</div>
        </div>
        <div class="stat-item">
          <div class="stat-value gradient-text">{{ distributorInfo ? formatAmount(distributorInfo.availableCommission) : '--' }}</div>
          <div class="stat-label">可提现</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">{{ distributorInfo?.totalFans || 0 }}</div>
          <div class="stat-label">团队人数</div>
        </div>
      </div>
      <div class="level-info" v-if="distributorInfo">
        <div class="level-badge glass-card">
          {{ distributorInfo.levelName || `Lv.${distributorInfo.level}` }}
        </div>
      </div>
    </div>

    <!-- Promotion Code -->
    <div class="promo-section glass-card" v-if="distributorInfo">
      <div class="promo-label">推广码</div>
      <div class="promo-code">{{ distributorInfo.promotionCode }}</div>
      <van-button
        round
        size="small"
        type="primary"
        plain
        @click="copyCode"
      >
        复制
      </van-button>
    </div>

    <!-- Withdraw Button -->
    <van-button
      round
      block
      type="primary"
      class="withdraw-btn"
      @click="handleWithdraw"
      :disabled="!distributorInfo || distributorInfo.availableCommission <= 0"
    >
      申请提现
    </van-button>

    <!-- Team List -->
    <div class="section">
      <div class="section-title">团队成员 ({{ distributorInfo?.totalFans || 0 }})</div>
      <div v-if="teamList.length === 0" class="empty-text">暂无团队成员</div>
      <div
        v-for="member in teamList"
        :key="member.userId"
        class="member-item glass-card"
      >
        <van-image
          round
          width="36"
          height="36"
          :src="member.avatar || DEFAULT_AVATAR"
        />
        <div class="member-info">
          <div class="member-nickname">{{ member.nickname }}</div>
          <div class="member-time">{{ member.joinTime }}</div>
        </div>
      </div>
    </div>

    <!-- Commission Records -->
    <div class="section">
      <div class="section-title">佣金记录</div>
      <div v-if="commissionRecords.length === 0" class="empty-text">暂无记录</div>
      <div
        v-for="record in commissionRecords"
        :key="record.id"
        class="record-item glass-card"
      >
        <div class="record-left">
          <div class="record-source">{{ record.source }}</div>
          <div class="record-time">{{ record.createTime }}</div>
        </div>
        <div class="record-amount" :class="{ positive: record.type === 'income' }">
          {{ record.type === 'income' ? '+' : '-' }}{{ formatAmount(record.amount) }}
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.distribution-page {
  padding: 16px;
}

.overview-card {
  padding: 24px 20px;
  margin-bottom: 16px;
  text-align: center;
}

.overview-title {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 20px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
  margin-bottom: 16px;
}

.stat-value {
  font-size: 22px;
  font-weight: 700;
}

.stat-label {
  font-size: 12px;
  color: var(--app-text-secondary);
  margin-top: 4px;
}

.level-badge {
  display: inline-block;
  padding: 4px 16px;
  font-size: 13px;
  color: var(--app-accent-light);
}

.promo-section {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px 20px;
  margin-bottom: 16px;
}

.promo-label {
  font-size: 14px;
  color: var(--app-text-secondary);
}

.promo-code {
  flex: 1;
  font-size: 18px;
  font-weight: 700;
  letter-spacing: 2px;
  font-family: monospace;
}

.withdraw-btn {
  margin-bottom: 24px;
  height: 44px;
  font-size: 16px;
  border: none;
  background: var(--app-gradient-2);
}

.section {
  margin-bottom: 24px;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 12px;
}

.empty-text {
  text-align: center;
  padding: 24px;
  color: var(--app-text-secondary);
  font-size: 14px;
}

.member-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  margin-bottom: 8px;
}

.member-info {
  flex: 1;
}

.member-nickname {
  font-size: 14px;
  font-weight: 500;
}

.member-time {
  font-size: 12px;
  color: var(--app-text-secondary);
  margin-top: 2px;
}

.record-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  margin-bottom: 8px;
}

.record-source {
  font-size: 14px;
}

.record-time {
  font-size: 12px;
  color: var(--app-text-secondary);
  margin-top: 2px;
}

.record-amount {
  font-size: 16px;
  font-weight: 600;
  color: var(--app-text-secondary);
}

.record-amount.positive {
  color: var(--app-success);
}
</style>
