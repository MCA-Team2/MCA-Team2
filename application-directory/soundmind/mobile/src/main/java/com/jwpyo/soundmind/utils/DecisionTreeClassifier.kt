package com.jwpyo.soundmind.utils

object DecisionTreeClassifier {
    private fun findMax(nums: IntArray): Int {
        var index = 0
        for (i in nums.indices) {
            index = if (nums[i] > nums[index]) i else index
        }
        return index
    }

    fun predict(features: DoubleArray): Int {
        val classes = IntArray(2)
        if (features[0] <= 98.1355209350586) {
            if (features[0] <= 83.72618865966797) {
                if (features[0] <= 79.17436981201172) {
                    if (features[0] <= 78.02802658081055) {
                        classes[0] = 982
                        classes[1] = 0
                    } else {
                        if (features[0] <= 78.03375625610352) {
                            classes[0] = 0
                            classes[1] = 1
                        } else {
                            if (features[3] <= 0.30097417533397675) {
                                classes[0] = 0
                                classes[1] = 1
                            } else {
                                classes[0] = 79
                                classes[1] = 0
                            }
                        }
                    }
                } else {
                    if (features[1] <= 81.23480606079102) {
                        if (features[0] <= 79.18359375) {
                            classes[0] = 0
                            classes[1] = 1
                        } else {
                            if (features[0] <= 81.91123580932617) {
                                if (features[0] <= 79.2327651977539) {
                                    if (features[0] <= 79.22829055786133) {
                                        classes[0] = 3
                                        classes[1] = 0
                                    } else {
                                        classes[0] = 0
                                        classes[1] = 1
                                    }
                                } else {
                                    classes[0] = 163
                                    classes[1] = 0
                                }
                            } else {
                                if (features[0] <= 81.92713165283203) {
                                    classes[0] = 0
                                    classes[1] = 1
                                } else {
                                    if (features[2] <= 0.3816770315170288) {
                                        classes[0] = 47
                                        classes[1] = 0
                                    } else {
                                        if (features[1] <= 61.371212005615234) {
                                            classes[0] = 0
                                            classes[1] = 2
                                        } else {
                                            if (features[3] <= 0.7864096462726593) {
                                                classes[0] = 20
                                                classes[1] = 0
                                            } else {
                                                if (features[3] <= 0.8289534747600555) {
                                                    classes[0] = 0
                                                    classes[1] = 1
                                                } else {
                                                    if (features[2] <= 0.532051295042038) {
                                                        classes[0] = 9
                                                        classes[1] = 0
                                                    } else {
                                                        if (features[1] <= 75.8985481262207) {
                                                            if (features[3] <= 2.013163685798645) {
                                                                classes[0] = 0
                                                                classes[1] = 2
                                                            } else {
                                                                classes[0] = 1
                                                                classes[1] = 0
                                                            }
                                                        } else {
                                                            classes[0] = 3
                                                            classes[1] = 0
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        if (features[1] <= 83.7557487487793) {
                            if (features[2] <= 0.5735930800437927) {
                                if (features[3] <= 0.42934852838516235) {
                                    classes[0] = 0
                                    classes[1] = 1
                                } else {
                                    classes[0] = 4
                                    classes[1] = 0
                                }
                            } else {
                                if (features[3] <= 0.3720332831144333) {
                                    classes[0] = 1
                                    classes[1] = 0
                                } else {
                                    classes[0] = 0
                                    classes[1] = 7
                                }
                            }
                        } else {
                            if (features[3] <= 0.359552726149559) {
                                if (features[0] <= 80.18342208862305) {
                                    classes[0] = 0
                                    classes[1] = 1
                                } else {
                                    classes[0] = 2
                                    classes[1] = 0
                                }
                            } else {
                                classes[0] = 12
                                classes[1] = 0
                            }
                        }
                    }
                }
            } else {
                if (features[2] <= 0.4369034916162491) {
                    if (features[2] <= 0.36323411762714386) {
                        classes[0] = 183
                        classes[1] = 0
                    } else {
                        if (features[1] <= 58.334693908691406) {
                            if (features[0] <= 85.21520233154297) {
                                classes[0] = 0
                                classes[1] = 4
                            } else {
                                classes[0] = 2
                                classes[1] = 0
                            }
                        } else {
                            if (features[2] <= 0.4192362427711487) {
                                classes[0] = 23
                                classes[1] = 0
                            } else {
                                if (features[2] <= 0.41960108280181885) {
                                    classes[0] = 0
                                    classes[1] = 2
                                } else {
                                    classes[0] = 11
                                    classes[1] = 0
                                }
                            }
                        }
                    }
                } else {
                    if (features[0] <= 94.51468658447266) {
                        if (features[1] <= 82.38257217407227) {
                            if (features[3] <= 0.3328280746936798) {
                                classes[0] = 7
                                classes[1] = 0
                            } else {
                                if (features[3] <= 0.43721437454223633) {
                                    classes[0] = 0
                                    classes[1] = 10
                                } else {
                                    if (features[3] <= 0.6360626220703125) {
                                        if (features[2] <= 0.6484172344207764) {
                                            if (features[0] <= 88.91971206665039) {
                                                classes[0] = 12
                                                classes[1] = 0
                                            } else {
                                                if (features[3] <= 0.5212627351284027) {
                                                    classes[0] = 0
                                                    classes[1] = 1
                                                } else {
                                                    classes[0] = 2
                                                    classes[1] = 0
                                                }
                                            }
                                        } else {
                                            classes[0] = 0
                                            classes[1] = 4
                                        }
                                    } else {
                                        if (features[0] <= 89.19792556762695) {
                                            if (features[0] <= 84.11428451538086) {
                                                if (features[1] <= 71.52863311767578) {
                                                    classes[0] = 2
                                                    classes[1] = 0
                                                } else {
                                                    classes[0] = 0
                                                    classes[1] = 4
                                                }
                                            } else {
                                                classes[0] = 0
                                                classes[1] = 13
                                            }
                                        } else {
                                            if (features[3] <= 1.4713618159294128) {
                                                classes[0] = 5
                                                classes[1] = 0
                                            } else {
                                                if (features[2] <= 0.511649563908577) {
                                                    classes[0] = 2
                                                    classes[1] = 0
                                                } else {
                                                    classes[0] = 0
                                                    classes[1] = 3
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            classes[0] = 7
                            classes[1] = 0
                        }
                    } else {
                        classes[0] = 0
                        classes[1] = 10
                    }
                }
            }
        } else {
            if (features[2] <= 0.21554985642433167) {
                if (features[0] <= 117.67396926879883) {
                    classes[0] = 28
                    classes[1] = 0
                } else {
                    classes[0] = 0
                    classes[1] = 8
                }
            } else {
                if (features[3] <= 0.2117670401930809) {
                    classes[0] = 2
                    classes[1] = 0
                } else {
                    if (features[1] <= 60.90468978881836) {
                        classes[0] = 0
                        classes[1] = 40
                    } else {
                        if (features[2] <= 0.43927648663520813) {
                            if (features[2] <= 0.42612265050411224) {
                                classes[0] = 0
                                classes[1] = 1
                            } else {
                                classes[0] = 3
                                classes[1] = 0
                            }
                        } else {
                            classes[0] = 0
                            classes[1] = 9
                        }
                    }
                }
            }
        }
        return findMax(classes)
    }
}